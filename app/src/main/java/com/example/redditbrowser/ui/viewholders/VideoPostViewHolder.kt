package com.example.redditbrowser.ui.viewholders

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.ui.FullscreenPostActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import kotlinx.android.synthetic.main.video_post.view.*

class VideoPostViewHolder(
    cardView: View,
    private val context: Context,
    private val showNsfw: Boolean,
    private val dataSourceFactory: DataSource.Factory
) :
    RecyclerView.ViewHolder(cardView) {
    private val titleView = cardView.titleView
    private val subredditView = cardView.subredditView
    private val authorView = cardView.authorView
    private val videoView = cardView.videoView
    private val clickableFrame = cardView.clickableFrame

    private var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null

    private var post: Post? = null

    companion object {
        fun create(
            parent: ViewGroup,
            context: Context,
            showNsfw: Boolean,
            factory: DataSource.Factory
        ): VideoPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.video_post, parent, false)
            return VideoPostViewHolder(view, context, showNsfw, factory)
        }
    }

    fun bind(post: Post?) {
        Log.d("VideoPost", "Bound ${post?.title}")
        this.post = post
        titleView.text = post?.title ?: "loading"
        subredditView.text = post?.subreddit ?: ""
        authorView.text = post?.author ?: ""

        if (post != null) {
            if (player == null) {
                player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
                player?.repeatMode = Player.REPEAT_MODE_ONE
                videoView.player = player
            }

            if (showNsfw or !post.nsfw) {
                mediaSource = if (post.type == Post.DASH) DashMediaSource.Factory(
                    DefaultDashChunkSource.Factory(dataSourceFactory),
                    dataSourceFactory
                ).createMediaSource(Uri.parse(post.url))
                else ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(post.url))
            }

            clickableFrame.setOnClickListener {
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
                putExtra("url", post!!.url)
            }
            context.startActivity(intent)
        }
    }

    fun play() {
        if (mediaSource != null) {
            player?.playWhenReady = true
            player?.prepare(mediaSource)
            player?.volume = 0f
        }
    }

    fun pause() {
        player?.stop()
    }

    fun release() {
        if (player != null) {
            player?.release()
            player = null
        }
    }
}
