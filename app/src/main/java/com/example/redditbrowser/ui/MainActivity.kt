package com.example.redditbrowser.ui

import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.redditbrowser.R
import com.example.redditbrowser.apis.ApiFetcher
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.datastructs.Feed.Companion.TYPE_FRONTPAGE
import com.example.redditbrowser.datastructs.Feed.Companion.TYPE_MULTIREDDIT
import com.example.redditbrowser.datastructs.Feed.Companion.TYPE_SUBREDDIT
import com.example.redditbrowser.datastructs.NetworkState
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.utils.ServiceProvider
import com.example.redditbrowser.web.GlideApp
import com.example.redditbrowser.web.HttpClientBuilder
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.Util
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.Cache
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val DEFAULT_FEED = ""
        const val DEFAULT_FEED_TYPE = TYPE_FRONTPAGE
        const val DEFAULT_COLS_LANDSCAPE = 3
        const val DEFAULT_COLS_PORTRAIT = 1
        const val DEFAULT_SPACING = 8f
    }

    private lateinit var model: FeedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)


        val cacheSize: Long = 150 * 1024 * 1024

        HttpClientBuilder.setCache(Cache(cacheDir, cacheSize))

        model = getViewModel()
        initList()
        initSwipeToRefresh()
        initMenu(navView)
        val feed = savedInstanceState?.getString("feed") ?: DEFAULT_FEED
        val feedType = savedInstanceState?.getInt("feedType") ?: DEFAULT_FEED_TYPE
        model.showFeed(Feed(feed, feedType))
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                showSettingsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        when (item.groupId) {
            R.id.nav_multis -> {
                updateFeed(item.title as String, TYPE_MULTIREDDIT)
            }

            R.id.nav_subscribed -> {
                updateFeed(item.title as String, TYPE_SUBREDDIT)
            }

            else -> {
                if (item.itemId == R.id.nav_frontpage) {
                    updateFeed(item.title as String, TYPE_FRONTPAGE)
                } else {
                    updateFeed(item.title as String, TYPE_SUBREDDIT)
                }
            }
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentFeed = model.currentFeed()
        outState.putString("feed", currentFeed?.feed)
        outState.putInt("feedType", currentFeed?.feedType!!)
    }

    private fun getViewModel(): FeedViewModel {
        val repository = ServiceProvider.instance(this.applicationContext as Application, false).getRepository()
        return ViewModelProviders.of(this, FeedViewModel.Factory(repository)).get(FeedViewModel::class.java)
    }

    private fun initList() {
        val glide = GlideApp.with(this@MainActivity)
        val dataSource = OkHttpDataSourceFactory(
            HttpClientBuilder.getClient(),
            Util.getUserAgent(this, "RedditBrowser"),
            DefaultBandwidthMeter()
        )

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val showNsfw = prefs.getBoolean("showNsfw", false)

        Log.d("Settings", "showNsfw: $showNsfw")

        val adapter = PostsAdapter(this, showNsfw, glide, dataSource)
        list.adapter = adapter
        model.posts.observe(this, Observer<PagedList<Post>> {
            adapter.submitList(it)
        })

        val spansLandscapeStr = prefs.getString("landCols", null)
        val spansPortraitStr = prefs.getString("portCols", null)

        val spansLandscape = spansLandscapeStr?.toInt() ?: DEFAULT_COLS_LANDSCAPE
        val spansPortrait = spansPortraitStr?.toInt() ?: DEFAULT_COLS_PORTRAIT

        val spanCount: Int =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) spansLandscape
            else spansPortrait
        (list.layoutManager as StaggeredGridLayoutManager).spanCount = spanCount

        val spacingStr = prefs.getString("cardSpacing", null)
        val spacing = spacingStr?.toFloat() ?: DEFAULT_SPACING

        val spacingPx: Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            spacing,
            resources.displayMetrics
        ).roundToInt()
        list.addItemDecoration(CardSpacer(spacingPx, spacingPx))
    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            model.refresh()
        }
    }

    private fun initMenu(navView: NavigationView) {
        val menu = navView.menu
        fetchMultis(menu)
        fetchSubreddits(menu)

        when (DEFAULT_FEED_TYPE) {
            TYPE_FRONTPAGE -> navView.setCheckedItem(R.id.nav_frontpage)
        }
    }

    private fun fetchSubreddits(menu: Menu) {
        ApiFetcher.getMySubscribedSubreddits(object : ApiFetcher.Listener<List<String>> {
            override fun onComplete(result: List<String>) {
                for (subreddit in result)
                    menu.add(R.id.nav_subscribed, Menu.NONE, Menu.NONE, subreddit)
            }

            override fun onFailure(t: Throwable) {
                Log.e("Subreddit fetching", t.localizedMessage)
            }
        })
    }

    private fun fetchMultis(menu: Menu) {
        ApiFetcher.getMyMultis(object : ApiFetcher.Listener<List<String>> {
            override fun onComplete(result: List<String>) {
                for (multi in result)
                    menu.add(R.id.nav_multis, Menu.NONE, Menu.NONE, multi)
            }

            override fun onFailure(t: Throwable) {
                Log.e("Multi fetching", t.localizedMessage)
            }
        })
    }

    private fun updateFeed(feed: String, feedType: Int) {
        if (model.showFeed(Feed(feed, feedType))) {
//            list.scrollToPosition(0)  // This causes StaggeredGridLayoutManager to draw views offscreen due to a bug
            (list.adapter as? PostsAdapter)?.submitList(null)
        }
    }

    private fun showSettingsActivity() {
        val activity = this
        val intent = Intent().apply {
            setClass(activity, SettingsActivity::class.java)
        }
        startActivity(intent)
    }
}
