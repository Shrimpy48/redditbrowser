package com.example.redditbrowser.ui.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post

class UrlPostViewHolder(cardView: View) : RecyclerView.ViewHolder(cardView) {
    private var post: Post? = null

    companion object {
        fun create(parent: ViewGroup): UrlPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.url_post, parent, false)
            return UrlPostViewHolder(view)
        }
    }

    fun bind(post: Post?) {
        this.post = post
    }
}

