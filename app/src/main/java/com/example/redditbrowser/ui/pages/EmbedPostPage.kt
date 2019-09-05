package com.example.redditbrowser.ui.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import kotlinx.android.synthetic.main.embed_page.view.*

class EmbedPostPage : Fragment() {

    private var type: Int = -1
    private var content: String? = null
    private var title: String? = null
    private var subreddit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt("type")
            content = it.getString("content")
            title = it.getString("title")
            subreddit = it.getString("subreddit")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.embed_page, container, false)
        when (type) {
            Post.EMBED -> view.embed_view.loadUrl(content)
            Post.EMBED_HTML -> view.embed_view.loadData(content, "text/html", null)
        }
        view.title_view.text = title
        view.subreddit_view.text = subreddit
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(post: Post): EmbedPostPage {
            return EmbedPostPage().apply {
                arguments = Bundle().apply {
                    putInt("type", post.type)
                    putString("content", post.content)
                    putString("title", post.title)
                    putString("subreddit", post.subreddit)
                }
            }
        }
    }
}