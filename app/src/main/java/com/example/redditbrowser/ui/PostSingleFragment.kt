package com.example.redditbrowser.ui

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.redditbrowser.R
import com.example.redditbrowser.utils.ServiceProvider
import kotlinx.android.synthetic.main.fragment_post_single.*
import kotlinx.android.synthetic.main.fragment_post_single.view.*

class PostSingleFragment : Fragment() {
    private var startPosition: Int? = null

    private lateinit var adapter: PostPagerAdapter

    private var feedModel: FeedViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_single, container, false)
        view.post_pager.adapter = adapter
        view.post_pager.currentItem = startPosition ?: 0 // Doesn't work until onResume
        return view
    }

    override fun onResume() {
        super.onResume()
        post_pager.currentItem = startPosition ?: 0
    }

    override fun onPause() {
        super.onPause()
        startPosition = getPosition()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        feedModel = getFeedViewModel()
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val useWebView = prefs.getBoolean("useWebView", false)
        adapter = PostPagerAdapter(childFragmentManager, useWebView)
        feedModel?.posts?.observe(
            this,
            Observer {
                adapter.submitList(it)
            })
    }

    private fun getFeedViewModel(): FeedViewModel? = activity?.run {
        val repository =
            ServiceProvider.instance(this.applicationContext as Application, false).getRepository()
        ViewModelProviders.of(this, FeedViewModel.Factory(repository))
            .get(FeedViewModel::class.java)
    }

    fun setPosition(position: Int) {
        startPosition = position
        post_pager?.currentItem = position
    }

    fun getPosition(): Int = post_pager.currentItem
}
