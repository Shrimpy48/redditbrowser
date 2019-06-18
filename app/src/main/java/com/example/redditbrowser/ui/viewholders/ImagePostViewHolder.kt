package com.example.redditbrowser.ui.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.web.GlideRequests

class ImagePostViewHolder(cardView: View, private val glide: GlideRequests) : RecyclerView.ViewHolder(cardView) {

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): ImagePostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.url_post, parent, false)
            return ImagePostViewHolder(view, glide)
        }
    }

    fun bind(post: Post?) {

    }
}