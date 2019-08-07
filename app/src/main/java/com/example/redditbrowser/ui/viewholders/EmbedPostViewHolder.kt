package com.example.redditbrowser.ui.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import kotlinx.android.synthetic.main.embed_post.view.*
import kotlinx.android.synthetic.main.post_info.view.*

class EmbedPostViewHolder(
    cardView: View,
    private val context: Context,
    private val showNsfw: Boolean
) :
    RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val embedView = cardView.embedView

    private var post: Post? = null

    companion object {
        fun create(parent: ViewGroup, context: Context, showNsfw: Boolean): EmbedPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.embed_post, parent, false)
            return EmbedPostViewHolder(view, context, showNsfw)
        }
    }

    fun bind(post: Post?) {
        this.post = post
        titleView.text = post?.title ?: context.getString(R.string.post_loading)
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        if (post != null) showEmbed(post)
    }

    private fun showEmbed(post: Post) {
        if (embedView.width == 0) {
            embedView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    embedView.viewTreeObserver.removeOnPreDrawListener(this)
                    showEmbed(post)
                    return true // == allow drawing
                }
            })
        } else {
            if (post.width != null && post.height != null) {
                val params = embedView.layoutParams
                params.height = post.height * embedView.width / post.width
                embedView.layoutParams = params
            } else {
                val params = embedView.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                embedView.layoutParams = params
            }

            if (showNsfw || !post.nsfw) {
                embedView.settings.javaScriptEnabled = true
                embedView.settings.useWideViewPort = true
                embedView.settings.loadWithOverviewMode = true
                if (post.type == Post.EMBED)
                    embedView.loadUrl(post.content)
                else if (post.type == Post.EMBED_HTML)
                    embedView.loadData(post.content, "text/html", null)
            }
        }
    }
}