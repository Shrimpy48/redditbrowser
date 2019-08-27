package com.example.redditbrowser.ui

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.redditbrowser.R
import com.example.redditbrowser.apis.AuthValues
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.utils.ServiceProvider
import com.example.redditbrowser.web.HttpClientBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import okhttp3.Cache

class MainActivity : AppCompatActivity(),
    AdapterView.OnItemSelectedListener, PostListFragment.OnFragmentInteractionListener {

    companion object {
        const val DEFAULT_FEED = ""
        const val DEFAULT_FEED_TYPE = Feed.TYPE_FRONTPAGE
        const val DEFAULT_SORT = ""
        const val DEFAULT_PERIOD = ""
    }

    private lateinit var feedModel: FeedViewModel
    private lateinit var navModel: NavViewModel

    private var postListFragment: PostListFragment? = null
    private var singlePostFragment: PostSingleFragment? = null

    private var username = AuthValues.redditUsername

    private var feed = DEFAULT_FEED
    private var feedType = DEFAULT_FEED_TYPE
    private var sort = DEFAULT_SORT
    private var period = DEFAULT_PERIOD


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = toolbar
        setSupportActionBar(toolbar)

//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        val drawerLayout: DrawerLayout = drawer_layout
        val navView: NavigationView = nav_view
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val sortSpinner = sort_spinner
        ArrayAdapter.createFromResource(this, R.array.sorts, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sortSpinner.adapter = adapter
            }
        sortSpinner.onItemSelectedListener = this

        val periodSpinner = period_spinner
        ArrayAdapter.createFromResource(this, R.array.periods, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                periodSpinner.adapter = adapter
            }
        periodSpinner.onItemSelectedListener = this


        val cacheSize: Long = 250 * 1024 * 1024  // 250 MiB
        HttpClientBuilder.setCache(Cache(cacheDir, cacheSize))

        initTheme()
        feedModel = getFeedViewModel()
        navModel = getNavViewModel()
        initNavView(navView)
        showListFragment()
        feed = savedInstanceState?.getString("feed") ?: DEFAULT_FEED
        feedType = savedInstanceState?.getInt("feedType") ?: DEFAULT_FEED_TYPE
        sort = savedInstanceState?.getString("sort") ?: DEFAULT_SORT
        period = savedInstanceState?.getString("period") ?: DEFAULT_PERIOD
        feedModel.showFeed(Feed(feed, feedType, sort, period))
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = drawer_layout
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val periodSpinner = period_spinner
        if (parent?.id == R.id.sort_spinner) {
            when (position) {
                0 -> { // default
                    sort = ""
                    period = ""
                    periodSpinner.visibility = View.INVISIBLE
                }
                1 -> { // hot
                    sort = "hot"
                    period = ""
                    periodSpinner.visibility = View.INVISIBLE
                }
                2 -> { // new
                    sort = "new"
                    period = ""
                    periodSpinner.visibility = View.INVISIBLE
                }
                3 -> { // controversial
                    sort = "controversial"
                    period = getPeriod(periodSpinner.selectedItemPosition)
                    periodSpinner.visibility = View.VISIBLE
                }
                4 -> { // top
                    sort = "top"
                    period = getPeriod(periodSpinner.selectedItemPosition)
                    periodSpinner.visibility = View.VISIBLE
                }
                5 -> { // rising
                    sort = "rising"
                    period = ""
                    periodSpinner.visibility = View.INVISIBLE
                }
            }
        } else {
            period = getPeriod(periodSpinner.selectedItemPosition)
        }
        updateFeed(feed, feedType, sort, period)
    }

    private fun getPeriod(position: Int) = when (position) {
        0 -> ""
        1 -> "hour"
        2 -> "day"
        3 -> "week"
        4 -> "month"
        5 -> "year"
        6 -> "all"
        else -> ""
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("feed", feed)
        outState.putInt("feedType", feedType)
        outState.putString("sort", sort)
        outState.putString("period", period)
        outState.putString("username", username)
    }

    override fun onListClicked(position: Int) {
        showSingleFragment(position)
    }

    private fun getFeedViewModel(): FeedViewModel {
        val repository = ServiceProvider.instance(this.applicationContext as Application, false).getRepository()
        return ViewModelProviders.of(this, FeedViewModel.Factory(repository)).get(FeedViewModel::class.java)
    }

    private fun getNavViewModel(): NavViewModel {
        return ViewModelProviders.of(this).get(NavViewModel::class.java)
    }

    private fun showListFragment(position: Int = 0) {
        if (postListFragment == null) postListFragment = PostListFragment()
        postListFragment?.setPosition(position)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentFrame, postListFragment!!)
        fragmentTransaction.commit()
    }

    private fun showSingleFragment(position: Int) {
        if (singlePostFragment == null) singlePostFragment = PostSingleFragment()
        singlePostFragment?.setPosition(position)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentFrame, singlePostFragment!!)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun initNavView(navView: NavigationView) {
        val nameView = navView.usernameView
        val multiList = navView.nav_list_multis
        val subredditList = navView.nav_list_subs
        val frontView = navView.nav_list_frontpage
        val popularView = navView.nav_list_popular
        val allView = navView.nav_list_all
        val subEntryView = navView.nav_entry_subreddit

        navModel.username.observe(this, Observer {
            nameView.text = it
        })

        frontView.setOnClickListener {
            feed = ""
            feedType = Feed.TYPE_FRONTPAGE
            updateFeed(feed, feedType, sort, period)
        }

        popularView.setOnClickListener {
            feed = "popular"
            feedType = Feed.TYPE_SUBREDDIT
            updateFeed(feed, feedType, sort, period)
        }

        allView.setOnClickListener {
            feed = "all"
            feedType = Feed.TYPE_SUBREDDIT
            updateFeed(feed, feedType, sort, period)
        }

        val multiAdapter = MultisAdapter { multi ->
            feed = multi
            feedType = Feed.TYPE_MULTIREDDIT
            updateFeed(feed, feedType, sort, period)
        }
        multiList.adapter = multiAdapter
        navModel.multis.observe(this, Observer {
            multiAdapter.submitList(it)
        })

        val subAdapter = SubredditsAdapter { sub ->
            feed = sub
            feedType = Feed.TYPE_SUBREDDIT
            updateFeed(feed, feedType, sort, period)
        }
        subredditList.adapter = subAdapter
        navModel.subreddits.observe(this, Observer {
            subAdapter.submitList(it)
        })

        subEntryView.setOnEditorActionListener { _, actionId, _ ->
            Log.d("Input", "EditorAction $actionId")
            if (actionId == EditorInfo.IME_ACTION_GO) {
                Log.d("Input", "Received ${subEntryView.text}")
                fromEntry(subEntryView)
                true
            } else {
                false
            }
        }

        subEntryView.setOnKeyListener { _, keyCode, event ->
            Log.d("Input", "Key $keyCode")
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                Log.d("Input", "Received ${subEntryView.text}")
                fromEntry(subEntryView)
                true
            } else {
                false
            }
        }
    }

    private fun fromEntry(subEntryView: TextView) {
        subEntryView.text.trim().toString().let {
            if (it.isNotEmpty()) {
                feed = it
                feedType = Feed.TYPE_SUBREDDIT
                updateFeed(feed, feedType, sort, period)
            }
        }
    }

    private fun updateFeed(feed: String, feedType: Int, sort: String = "", period: String = "") =
        feedModel.showFeed(Feed(feed, feedType, sort, period))

    private fun showSettingsActivity() {
        val activity = this
        val intent = Intent().apply {
            setClass(activity, SettingsActivity::class.java)
        }
        startActivity(intent)
    }

    private fun initTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        when (prefs.getString("themeMode", "2")) {
            "0" -> setDefaultNightMode(MODE_NIGHT_NO)
            "1" -> setDefaultNightMode(MODE_NIGHT_YES)
            "2" -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            "3" -> setDefaultNightMode(MODE_NIGHT_AUTO_BATTERY)
        }
    }
}
