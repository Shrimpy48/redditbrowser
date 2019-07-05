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
import com.example.redditbrowser.web.GlideRequests
import kotlinx.android.synthetic.main.image_post.view.*

class ImagePostViewHolder(
    cardView: View,
    private val context: Context,
    private val showNsfw: Boolean,
    private val glide: GlideRequests
) :
    RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val imageView = cardView.imageView

    private var post: Post? = null

    companion object {
        fun create(parent: ViewGroup, context: Context, showNsfw: Boolean, glide: GlideRequests): ImagePostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_post, parent, false)
            return ImagePostViewHolder(view, context, showNsfw, glide)
        }
    }

    fun bind(post: Post?) {
        this.post = post
        titleView.text = post?.title ?: "loading"
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        val isNsfw = post?.nsfw ?: false
        if (showNsfw or !isNsfw)
            glide.load(post?.contentUrl)
                .placeholder(R.drawable.ic_image_black_24dp)
                .error(R.drawable.ic_error_black_24dp)
                .into(imageView)
        else
            glide.load(R.drawable.ic_warning_black_24dp)
                .into(imageView)
        if (post != null)
            imageView.setOnClickListener {
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

    fun clear() {
        glide.clear(imageView)
    }
}
