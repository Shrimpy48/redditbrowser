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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString("url")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.url_page, container, false)
        view.url_view.text = url
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(post: Post): UrlPostPage {
            return UrlPostPage().apply {
                arguments = Bundle().apply {
                    putString("url", post.postUrl)
                }
            }
        }
    }
}