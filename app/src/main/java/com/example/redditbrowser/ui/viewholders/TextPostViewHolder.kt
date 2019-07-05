package com.example.redditbrowser.ui.viewholders

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.ui.FullscreenPostActivity
import kotlinx.android.synthetic.main.text_post.view.*

class TextPostViewHolder(cardView: View, private val context: Context, private val showNsfw: Boolean) :
    RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val selftextView = cardView.selftextView

    private var post: Post? = null

    companion object {
        fun create(parent: ViewGroup, context: Context, showNsfw: Boolean): TextPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.text_post, parent, false)
            return TextPostViewHolder(view, context, showNsfw)
        }
    }

    fun bind(post: Post?) {
        this.post = post
        titleView.text = post?.title ?: "loading"
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        val isNsfw = post?.nsfw ?: false
        if (showNsfw or !isNsfw)
            selftextView.text = post?.selftext ?: ""
        else selftextView.text = "NSFW"

        if (post != null)
            selftextView.setOnClickListener {
                showFullscreen()
            }
    }

    private fun showFullscreen() {
        if (post != null) {
            val intent = Intent().apply {
                setClass(context, FullscreenPostActivity::class.java)
                putExtra("type", post!!.type)
                putExtra("title", post!!.title)
                putExtra("subreddit", post!!.subreddit)
                putExtra("author", post!!.author)
                putExtra("selftext", post!!.selftext)
                putExtra("url", post!!.contentUrl)
            }
            context.startActivity(intent)
        }
    }
}
