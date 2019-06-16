package com.example.redditbrowser

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

class FullscreenPostActivity : AppCompatActivity() {
    private var url: Uri = Uri.EMPTY
    private lateinit var type: PostType
    private lateinit var image: ImageView
    private lateinit var video: PlayerView
    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_post)

        if (intent.data != null) url = intent.data!!
        type = intent.extras?.getSerializable("type") as PostType

        image = findViewById(R.id.fullscreen_image)
        video = findViewById(R.id.fullscreen_video)

        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(this),
            DefaultTrackSelector(), DefaultLoadControl()
        )
        video.player = player
        player.playWhenReady = true
        player.seekTo(0, 0)
    }

    override fun onStart() {
        super.onStart()

        when (type) {
            PostType.IMAGE -> {
                image.visibility = VISIBLE
                GlideApp.with(this)
                    .load(url)
                    .into(image)
            }
            PostType.VIDEO -> {
                video.visibility = VISIBLE
                val mediaSource = ExtractorMediaSource.Factory(
                    DefaultHttpDataSourceFactory(
                        Util.getUserAgent(
                            this,
                            "RedditBrowser"
                        )
                    )
                )
                    .createMediaSource(url)
                player.prepare(mediaSource, true, false)
            }
            PostType.VIDEO_DASH -> {
                video.visibility = VISIBLE
                val dataSourceFactory = DefaultHttpDataSourceFactory(
                    Util.getUserAgent(
                        this,
                        "RedditBrowser"
                    )
                ) as DataSource.Factory
                val mediaSource =
                    DashMediaSource.Factory(DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory)
                        .createMediaSource(url)
                player.prepare(mediaSource, true, false)
            }
            else -> {
                Log.w("Fullscreen viewer", "Invalid PostType")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        image.visibility = GONE
        video.visibility = GONE
        GlideApp.with(this)
            .clear(image)
        player.release()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}
