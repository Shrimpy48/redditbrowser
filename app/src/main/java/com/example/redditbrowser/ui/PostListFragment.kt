package com.example.redditbrowser.ui

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.NetworkState
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.utils.ServiceProvider
import com.example.redditbrowser.web.GlideApp
import com.example.redditbrowser.web.HttpClientBuilder
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_post_list.*
import kotlinx.android.synthetic.main.fragment_post_list.view.*
import kotlin.math.roundToInt


class PostListFragment : Fragment() {
    private var startPosition: Int? = null

    private var listener: OnFragmentInteractionListener? = null
    private var feedModel: FeedViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_list, container, false)
        initList(view.list)
        initSwipeToRefresh(view.swipe_refresh)
        return view
    }

    private fun onItemPressed(position: Int) {
        listener?.onListClicked(position)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
        feedModel = getFeedViewModel()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun getFeedViewModel(): FeedViewModel? = activity?.run {
        val repository =
            ServiceProvider.instance(this.applicationContext as Application, false).getRepository()
        ViewModelProviders.of(this, FeedViewModel.Factory(repository))
            .get(FeedViewModel::class.java)
    }

    private fun initList(list: RecyclerView) {
        activity?.let { activity ->
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val showNsfw = prefs.getBoolean("showNsfw", false)
            val autoPlay = prefs.getBoolean("autoPlay", false)
            val useWebView = prefs.getBoolean("useWebView", false)
            val useOkHttpExoPlayer = prefs.getBoolean("useOkHttpExoPlayer", true)
            val spansLandscapeStr = prefs.getString("landCols", null)
            val spansPortraitStr = prefs.getString("portCols", null)
            val spacingStr = prefs.getString("cardSpacing", null)

            val glide = GlideApp.with(activity)
            val dataSource = if (useOkHttpExoPlayer) OkHttpDataSourceFactory(
                HttpClientBuilder.getClient(),
                Util.getUserAgent(activity, "RedditBrowser"),
                DefaultBandwidthMeter()
            ) else DefaultDataSourceFactory(
                activity,
                Util.getUserAgent(activity, "RedditBrowser")
            )

            val adapter = PostsAdapter(
                activity,
                showNsfw,
                autoPlay,
                useWebView,
                glide,
                dataSource
            ) { onItemPressed(it) }
            list.adapter = adapter
            feedModel?.posts?.observe(this, Observer<PagedList<Post>> {
                adapter.submitList(it)
            })

            val spansLandscape = spansLandscapeStr?.toInt() ?: DEFAULT_COLS_LANDSCAPE
            val spansPortrait = spansPortraitStr?.toInt() ?: DEFAULT_COLS_PORTRAIT

            val spanCount: Int =
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) spansLandscape
                else spansPortrait
            (list.layoutManager as StaggeredGridLayoutManager).spanCount = spanCount

            val spacing = spacingStr?.toFloat() ?: DEFAULT_SPACING

            val spacingPx: Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                spacing,
                resources.displayMetrics
            ).roundToInt()
            list.addItemDecoration(CardSpacer(spacingPx, spacingPx))

            list.scrollToPosition(startPosition ?: 0)
        }
    }

    private fun initSwipeToRefresh(swipe_refresh: SwipeRefreshLayout) {
        feedModel?.refreshState?.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            feedModel?.refresh()
        }
    }

    fun setPosition(position: Int) {
        startPosition = position
        list?.scrollToPosition(startPosition!!)
    }

    fun getPosition(): Int {
        val layoutManager = list.layoutManager as StaggeredGridLayoutManager
        var array = IntArray(layoutManager.spanCount)
        array = layoutManager.findFirstCompletelyVisibleItemPositions(array)
        return array[0]
    }

    interface OnFragmentInteractionListener {
        fun onListClicked(position: Int)
    }

    companion object {
        const val DEFAULT_COLS_LANDSCAPE = 3
        const val DEFAULT_COLS_PORTRAIT = 1
        const val DEFAULT_SPACING = 8f
    }
}
