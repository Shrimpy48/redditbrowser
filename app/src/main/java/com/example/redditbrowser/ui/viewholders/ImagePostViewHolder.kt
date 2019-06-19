package com.example.redditbrowser.ui.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.web.GlideRequests
import kotlinx.android.synthetic.main.image_post.view.*

class ImagePostViewHolder(cardView: View, private val glide: GlideRequests) : RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val imageView = cardView.imageView

    private var post: Post? = null

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): ImagePostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_post, parent, false)
            return ImagePostViewHolder(view, glide)
        }
    }

    fun bind(post: Post?) {
        this.post = post
        titleView.text = post?.title ?: "loading"
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        glide.load(post?.url)
            .into(imageView)
    }
}
