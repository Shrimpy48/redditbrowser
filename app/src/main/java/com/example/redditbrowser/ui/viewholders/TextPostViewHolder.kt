package com.example.redditbrowser.ui.viewholders

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import kotlinx.android.synthetic.main.text_post.view.*

class TextPostViewHolder(cardView: View, private val showNsfw: Boolean) : RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val selftextView = cardView.selftextView

    private var post: Post? = null

    companion object {
        fun create(parent: ViewGroup, showNsfw: Boolean): TextPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.text_post, parent, false)
            return TextPostViewHolder(view, showNsfw)
        }
    }

    fun bind(post: Post?) {
        Log.d("TextPost", "Bound ${post?.title}")
        this.post = post
        titleView.text = post?.title ?: "loading"
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        val isNsfw = post?.nsfw ?: false
        if (showNsfw or !isNsfw)
            selftextView.text = post?.selftext ?: ""
    }
}
