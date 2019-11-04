package com.example.redditbrowser.ui.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import kotlinx.android.synthetic.main.url_page.view.*

class UrlPostPage : Fragment() {

    private var url: String? = null
    private var title: String? = null
    private var subreddit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString("url")
            title = it.getString("title")
            subreddit = it.getString("subreddit")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.url_page, container, false)
        view.url_view.text = url
        view.title_view.text = title
        view.subreddit_view.text = subreddit
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(post: Post): UrlPostPage {
            return UrlPostPage().apply {
                arguments = Bundle().apply {
                    putString("url", post.postUrl)
                    putString("title", post.title)
                    putString("subreddit", post.subreddit)
                }
            }
        }
    }
}