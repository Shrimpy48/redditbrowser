package com.example.redditbrowser.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
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
import com.example.redditbrowser.database.PostDatabase
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.datastructs.Feed.Companion.TYPE_SPECIAL
import com.example.redditbrowser.datastructs.NetworkState
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.repositories.PostRepository
import com.example.redditbrowser.web.GlideApp
import com.example.redditbrowser.web.HttpClientBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.Cache
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val DEFAULT_FEED = "Front Page"
        const val DEFAULT_FEED_TYPE = TYPE_SPECIAL
        const val DEFAULT_COLS_LANDSCAPE = 2
        const val DEFAULT_COLS_PORTRAIT = 1
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
        initAdapter()
        initManager()
        initSwipeToRefresh()
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

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
        val db by lazy { PostDatabase.create(this, true) }
        val executor = Executors.newFixedThreadPool(6)
        val repository = PostRepository(db, executor)
        return ViewModelProviders.of(this, FeedViewModel.Factory(repository)).get(FeedViewModel::class.java)
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        val adapter = PostsAdapter(glide) {
            model.retry()
        }
        list.adapter = adapter
        model.posts.observe(this, Observer<PagedList<Post>> {
            adapter.submitList(it)
        })
    }

    private fun initManager() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val spansLandscape = prefs.getInt("cols_landscape", DEFAULT_COLS_LANDSCAPE)
        val spansPortrait = prefs.getInt("cols_portrait", DEFAULT_COLS_PORTRAIT)

        val spanCount: Int =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) spansLandscape
            else spansPortrait
        val viewManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        list.layoutManager = viewManager
        list.addItemDecoration(CardSpacer(8, 8))
    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            model.refresh()
        }
    }

    private fun updateFeed(feed: String, feedType: Int) {
        if (model.showFeed(Feed(feed, feedType))) {
            list.scrollToPosition(0)
            (list.adapter as? PostsAdapter)?.submitList(null)
        }
    }
}
