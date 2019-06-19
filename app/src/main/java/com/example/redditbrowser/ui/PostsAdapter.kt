package com.example.redditbrowser.ui

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.ui.viewholders.ImagePostViewHolder
import com.example.redditbrowser.ui.viewholders.TextPostViewHolder
import com.example.redditbrowser.ui.viewholders.UrlPostViewHolder
import com.example.redditbrowser.ui.viewholders.VideoPostViewHolder
import com.example.redditbrowser.web.GlideRequests
import com.google.android.exoplayer2.upstream.DataSource

class PostsAdapter(
    private val context: Context,
    private val glide: GlideRequests,
    private val dataSource: DataSource.Factory
) :
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
            R.layout.video_post -> (holder as VideoPostViewHolder).bind(getItem(position), context)
            R.layout.url_post -> (holder as UrlPostViewHolder).bind(getItem(position))
            else -> throw IllegalArgumentException("unknown view type ${getItemViewType(position)}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.text_post -> TextPostViewHolder.create(parent)
            R.layout.image_post -> ImagePostViewHolder.create(parent, glide)
            R.layout.video_post -> VideoPostViewHolder.create(parent, dataSource)
            R.layout.url_post -> UrlPostViewHolder.create(parent)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemViewType == R.layout.video_post) (holder as VideoPostViewHolder).release()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.type) {
            Post.TEXT -> R.layout.text_post
            Post.IMAGE -> R.layout.image_post
            Post.VIDEO -> R.layout.video_post
            Post.DASH -> R.layout.video_post
            Post.URL -> R.layout.url_post
            else -> throw IllegalArgumentException("Invalid post type ${getItem(position)?.type}")
        }
    }

}
