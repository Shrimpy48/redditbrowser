package com.example.redditbrowser.ui.viewholders

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.web.GlideRequests
import kotlinx.android.synthetic.main.image_post.view.*

class ImagePostViewHolder(cardView: View, private val showNsfw: Boolean, private val glide: GlideRequests) :
    RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val imageView = cardView.imageView

    private var post: Post? = null

    companion object {
        fun create(parent: ViewGroup, showNsfw: Boolean, glide: GlideRequests): ImagePostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_post, parent, false)
            return ImagePostViewHolder(view, showNsfw, glide)
        }
    }

    fun bind(post: Post?) {
        Log.d("ImagePost", "Bound ${post?.title}")
        this.post = post
        titleView.text = post?.title ?: "loading"
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        val isNsfw = post?.nsfw ?: false
        if (showNsfw or !isNsfw)
            glide.load(post?.url)
                .placeholder(R.drawable.ic_insert_photo_black_48dp)
                .into(imageView)
        else
            glide.load(R.drawable.ic_insert_photo_black_48dp)
                .into(imageView)
    }

    fun clear() {
        glide.clear(imageView)
    }
}
