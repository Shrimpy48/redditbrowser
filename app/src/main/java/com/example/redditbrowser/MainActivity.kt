package com.example.redditbrowser

import android.os.Bundle
import android.util.Log
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
import com.google.android.material.navigation.NavigationView
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Credentials
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var apiService: RedditApiService
    private lateinit var apiServiceOauth: RedditApiService
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var tokenResp: Single<AuthResponse>
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

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.reddit.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val retrofitOauth: Retrofit = Retrofit.Builder()
            .baseUrl("https://oauth.reddit.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        apiService = retrofit.create(RedditApiService::class.java)
        apiServiceOauth = retrofitOauth.create(RedditApiService::class.java)

        compositeDisposable = CompositeDisposable()

        tokenResp = apiService.getAuth(
            Credentials.basic(AuthValues.redditId, AuthValues.redditSecret),
            "password",
            AuthValues.redditUsername,
            AuthValues.redditPassword
        ).cache()

        currentFeed = "Front page"
        currentFeedType = FeedType.SPECIAL

        viewManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        viewAdapter = CardsAdapter(apiServiceOauth, tokenResp, currentFeed, currentFeedType)
        recyclerView = findViewById<RecyclerView>(R.id.cards_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.addItemDecoration(CardSpacer(8, 8))

        val menu = findViewById<NavigationView>(R.id.nav_view).menu

        navView.setCheckedItem(R.id.nav_frontpage)

        tokenResp.flatMap { firstResponse ->
            apiServiceOauth.getMyMultis(
                firstResponse.tokenType + " " + firstResponse.accessToken
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<MultiInfoWrapperBasic>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(resp: List<MultiInfoWrapperBasic>) {
                    for (i in 0 until resp.size)
                        menu.add(R.id.nav_multireddits, NONE, NONE, resp[i].data?.displayName)
                }

                override fun onError(e: Throwable) {
                    Log.e("Menu", e.localizedMessage)
                }
            })

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

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }

    private fun fetchSubreddits(menu: Menu) {
        tokenResp.flatMap { firstResponse ->
            apiServiceOauth.getMySubscribedSubreddits(
                firstResponse.tokenType + " " + firstResponse.accessToken
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<SubredditInfoListWrapper> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(resp: SubredditInfoListWrapper) {
                    val subreddits = resp.data?.children
                    val numSubreddits = resp.data?.dist
                    if (subreddits != null && numSubreddits != null)
                        for (i in 0 until numSubreddits)
                            menu.add(R.id.nav_subscribed, NONE, NONE, subreddits[i].data?.displayName)
                    if (resp.data?.after != null)
                        fetchSubreddits(menu, resp.data?.after!!, numSubreddits!!)
                }

                override fun onError(e: Throwable) {
                    Log.e("Menu", e.localizedMessage)
                }
            })
    }

    private fun fetchSubreddits(menu: Menu, after: String, count: Int) {
        tokenResp.flatMap { firstResponse ->
            apiServiceOauth.getMySubscribedSubreddits(
                firstResponse.tokenType + " " + firstResponse.accessToken,
                after, count
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<SubredditInfoListWrapper> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(resp: SubredditInfoListWrapper) {
                    val subreddits = resp.data?.children
                    val numSubreddits = resp.data?.dist
                    if (subreddits != null && numSubreddits != null)
                        for (i in 0 until numSubreddits)
                            menu.add(R.id.nav_subscribed, NONE, NONE, subreddits[i].data?.displayName)
                    if (resp.data?.after != null)
                        fetchSubreddits(menu, resp.data?.after!!, count + numSubreddits!!)
                }

                override fun onError(e: Throwable) {
                    Log.e("Menu", e.localizedMessage)
                }
            })
    }

    private fun changeFeed(newFeed: String, newType: FeedType) {
        if (currentFeed != newFeed || currentFeedType != newType) {
            currentFeed = newFeed
            currentFeedType = newType
            viewAdapter = CardsAdapter(apiServiceOauth, tokenResp, currentFeed, currentFeedType)
            recyclerView.swapAdapter(viewAdapter, false)
        }
    }
}



