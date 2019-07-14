package com.example.redditbrowser.ui.viewholders

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
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
    private val autoPlay: Boolean,
    private val glide: GlideRequests
) :
    RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val imageView = cardView.imageView

    private var post: Post? = null

    companion object {
        fun create(
            parent: ViewGroup,
            context: Context,
            showNsfw: Boolean,
            autoPlay: Boolean,
            glide: GlideRequests
        ): ImagePostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_post, parent, false)
            return ImagePostViewHolder(view, context, showNsfw, autoPlay, glide)
        }
    }

    fun bind(post: Post?) {
        this.post = post
        titleView.text = post?.title ?: "loading"
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        if (post != null) showImage(post)
    }

    private fun showImage(post: Post) {
        if (imageView.width == 0) {
            imageView.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    imageView.viewTreeObserver.removeOnPreDrawListener(this)
                    showImage(post)
                    return true // == allow drawing
                }
            })
        } else {
            if (post.width != null && post.height != null) {
                val params = imageView.layoutParams
                params.height = post.height * imageView.width / post.width
                imageView.layoutParams = params
            }
            if (showNsfw or !post.nsfw) {
                var loader = glide.load(post.content)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .fitCenter()
                if (!autoPlay) loader = loader.dontAnimate()
                loader.into(imageView)
            } else
                glide.load(R.drawable.ic_warning_black_24dp)
                    .fitCenter()
                    .into(imageView)
            imageView.setOnClickListener {
                showFullscreen()
            }
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
                putExtra("url", post!!.content)
            }
            context.startActivity(intent)
        }
    }

    fun clear() {
        glide.clear(imageView)
    }
}
