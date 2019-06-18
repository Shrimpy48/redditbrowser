package com.example.redditbrowser.ui.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import kotlinx.android.synthetic.main.video_post.view.*

class VideoPostViewHolder(cardView: View) : RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val videoView = cardView.videoView

    private var post: Post? = null

    companion object {
        fun create(parent: ViewGroup): VideoPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.video_post, parent, false)
            return VideoPostViewHolder(view)
        }
    }

    fun bind(post: Post?) {
        this.post = post
        titleView.text = post?.title ?: "loading"
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        // TODO show video
    }
}
