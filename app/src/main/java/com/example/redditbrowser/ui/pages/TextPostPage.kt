package com.example.redditbrowser.ui.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import kotlinx.android.synthetic.main.text_page.view.*

class TextPostPage : Fragment() {

    private var selftext: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selftext = it.getString("selftext")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.text_page, container, false)
        view.text_view.text = selftext
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(post: Post): TextPostPage {
            return TextPostPage().apply {
                arguments = Bundle().apply {
                    putString("selftext", post.selftext)
                }
            }
        }
    }
}