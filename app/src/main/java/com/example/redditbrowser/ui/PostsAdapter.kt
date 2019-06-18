package com.example.redditbrowser.ui

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.GlideRequests
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post

class PostsAdapter(private val glide: GlideRequests, private val retryCallback: () -> Unit) :
    PagedListAdapter<Post, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Post>() {
            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.name == newItem.name
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.text_post -> (holder as TextPostViewHolder).bind(getItem(position))
            R.layout.image_post -> (holder as ImagePostViewHolder).bind(getItem(position))
            R.layout.video_post -> (holder as VideoPostViewHolder).bind(getItem(position))
            R.layout.url_post -> (holder as UrlPostViewHolder).bind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.text_post -> TextPostViewHolder.create(parent)
            R.layout.image_post -> ImagePostViewHolder.create(parent, glide)
            R.layout.video_post -> VideoPostViewHolder.create(parent)
            R.layout.url_post -> UrlPostViewHolder.create(parent)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

}
