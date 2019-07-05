package com.example.redditbrowser.ui.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import kotlinx.android.synthetic.main.url_post.view.*

class UrlPostViewHolder(cardView: View, private val showNsfw: Boolean) : RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val urlView = cardView.urlView

    private var post: Post? = null

    companion object {
        fun create(parent: ViewGroup, showNsfw: Boolean): UrlPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.url_post, parent, false)
            return UrlPostViewHolder(view, showNsfw)
        }
    }

    fun bind(post: Post?) {
        this.post = post
        titleView.text = post?.title ?: "loading"
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        urlView.text = post?.postUrl ?: ""
    }
}

