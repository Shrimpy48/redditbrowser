package com.example.redditbrowser.ui.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post

class VideoPostViewHolder(cardView: View) : RecyclerView.ViewHolder(cardView) {

    companion object {
        fun create(parent: ViewGroup): VideoPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.video_post, parent, false)
            return VideoPostViewHolder(view)
        }
    }

    fun bind(post: Post?) {

    }
}
