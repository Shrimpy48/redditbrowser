package com.example.redditbrowser.ui.viewholders

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import kotlinx.android.synthetic.main.post_info.view.*
import kotlinx.android.synthetic.main.video_post.view.*

class VideoPostViewHolder(
    cardView: View,
    private val context: Context,
    private val showNsfw: Boolean,
    private val autoPlay: Boolean,
    private val dataSourceFactory: DataSource.Factory
) :
    RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val videoView = cardView.videoView
    private val mediaLayout = cardView.mediaLayout

    private var player: SimpleExoPlayer? = null

    private var post: Post? = null

    companion object {
        fun create(
            parent: ViewGroup,
            context: Context,
            showNsfw: Boolean,
            autoPlay: Boolean,
            factory: DataSource.Factory
        ): VideoPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.video_post, parent, false)
            return VideoPostViewHolder(view, context, showNsfw, autoPlay, factory)
        }
    }

    fun bind(post: Post?, clickCallback: () -> Unit) {
        this.post = post
        titleView.text = post?.title ?: context.getString(R.string.post_loading)
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        if (post != null) {
            if (player == null) {
                player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
                player?.repeatMode = Player.REPEAT_MODE_ONE
                videoView.player = player
            }

            showVideo(post, clickCallback)
        }
    }

    private fun showVideo(post: Post, clickCallback: () -> Unit) {
        if (videoView.width == 0) {
            videoView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    videoView.viewTreeObserver.removeOnPreDrawListener(this)
                    showVideo(post, clickCallback)
                    return true // == allow drawing
                }
            })
        } else {
            if (post.width != null && post.height != null) {
                val params = videoView.layoutParams
                params.height = post.height * videoView.width / post.width
                videoView.layoutParams = params
            } else {
                val params = videoView.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                videoView.layoutParams = params
            }


            if (showNsfw or !post.nsfw) {
                val mediaSource = if (post.type == Post.VIDEO_DASH) DashMediaSource.Factory(
                    DefaultDashChunkSource.Factory(dataSourceFactory),
                    dataSourceFactory
                ).createMediaSource(Uri.parse(post.content))
                else ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(post.content))
                player?.volume = 0f
                player?.playWhenReady = autoPlay
                player?.prepare(mediaSource)
            }

            mediaLayout.setOnClickListener {
                clickCallback()
            }
        }
    }

    fun release() {
        if (player != null) {
            player?.release()
            player = null
        }
    }
}
