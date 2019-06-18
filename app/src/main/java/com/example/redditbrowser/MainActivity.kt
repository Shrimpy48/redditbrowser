package com.example.redditbrowser

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.Menu.NONE
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.redditbrowser.apis.ApiFetcher
import com.example.redditbrowser.web.HttpClientBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Cache


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var currentFeed: String
    private lateinit var currentFeedType: FeedType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        if (savedInstanceState != null) {
            currentFeed = if (savedInstanceState.containsKey("currentFeed"))
                savedInstanceState.getString("currentFeed")!!
            else "Front page"
            currentFeedType = if (savedInstanceState.containsKey("currentFeedType"))
                savedInstanceState.getSerializable("currentFeedType") as FeedType
            else FeedType.SPECIAL
        } else {
            currentFeed = "Front page"
            currentFeedType = FeedType.SPECIAL
        }

        val cacheSize: Long = 250 * 1024 * 1024

        HttpClientBuilder.setCache(Cache(cacheDir, cacheSize))

        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        val spanCountLandscape = prefs.getInt("cols_landscape", 2)
        val spanCountPortrait = prefs.getInt("cols_portrait", 1)

        val spanCount: Int =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) spanCountLandscape
            else spanCountPortrait

        viewManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        viewAdapter = CardsAdapter(this, currentFeed, currentFeedType)
        recyclerView = findViewById<RecyclerView>(R.id.cards_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.addItemDecoration(CardSpacer(8, 8))

        val menu = findViewById<NavigationView>(R.id.nav_view).menu

        navView.setCheckedItem(R.id.nav_frontpage)

        fetchMultis(menu)
        fetchSubreddits(menu)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putString("currentFeed", currentFeed)
        outState?.putSerializable("currentFeedType", currentFeedType)
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
        when (item.groupId) {
            R.id.nav_multireddits -> changeFeed(item.title as String, FeedType.MULTI)

            R.id.nav_subscribed -> changeFeed(item.title as String, FeedType.SUBREDDIT)

            else -> {
                if (item.title == "all" || item.title == "popular")
                    changeFeed(item.title as String, FeedType.SUBREDDIT)
                else changeFeed(item.title as String, FeedType.SPECIAL)
            }
        }

        nav_view.setCheckedItem(item)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun fetchSubreddits(menu: Menu) {
        ApiFetcher.getMySubscribedSubreddits(object : ApiFetcher.Listener<List<String?>?> {
            override fun onComplete(result: List<String?>?) {
                if (result != null)
                    for (subreddit in result)
                        if (subreddit != null)
                            menu.add(R.id.nav_subscribed, NONE, NONE, subreddit)
            }
        })
    }

    private fun fetchMultis(menu: Menu) {
        ApiFetcher.getMyMultis(object : ApiFetcher.Listener<List<String?>?> {
            override fun onComplete(result: List<String?>?) {
                if (result != null)
                    for (multi in result)
                        if (multi != null)
                            menu.add(R.id.nav_multireddits, NONE, NONE, multi)
            }
        })
    }

    private fun changeFeed(newFeed: String, newType: FeedType) {
        if (currentFeed != newFeed || currentFeedType != newType) {
            currentFeed = newFeed
            currentFeedType = newType
            viewAdapter = CardsAdapter(this, currentFeed, currentFeedType)
            recyclerView.swapAdapter(viewAdapter, false)
        }
    }
}
