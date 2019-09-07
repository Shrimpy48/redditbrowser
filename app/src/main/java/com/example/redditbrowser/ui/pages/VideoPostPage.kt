package com.example.redditbrowser.ui.pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.web.Downloader
import com.example.redditbrowser.web.HttpClientBuilder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.video_page.view.*

class VideoPostPage : Fragment() {

    private var player: ExoPlayer? = null
    private lateinit var mediaSource: MediaSource

    private var useOkHttpExoPlayer = true

    private var type: Int = -1
    private var content: String? = null
    private var title: String? = null
    private var subreddit: String? = null

    private var pendingUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt("type")
            content = it.getString("content")
            title = it.getString("title")
            subreddit = it.getString("subreddit")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        player?.repeatMode = Player.REPEAT_MODE_ONE
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        useOkHttpExoPlayer = prefs.getBoolean("useOkHttpExoPlayer", true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.video_page, container, false)
        when (type) {
            Post.VIDEO -> {
                activity?.let {
                    view.video_view.player = player
                    val dataSource = if (useOkHttpExoPlayer) OkHttpDataSourceFactory(
                        HttpClientBuilder.getClient(),
                        Util.getUserAgent(it, "RedditBrowser"),
                        DefaultBandwidthMeter()
                    ) else DefaultDataSourceFactory(
                        it,
                        Util.getUserAgent(it, "RedditBrowser")
                    )
                    mediaSource = ExtractorMediaSource.Factory(dataSource)
                        .createMediaSource(Uri.parse(content))
                }
            }
            Post.VIDEO_DASH -> {
                activity?.let {
                    player = ExoPlayerFactory.newSimpleInstance(it, DefaultTrackSelector())
                    view.video_view.player = player
                    val dataSource = OkHttpDataSourceFactory(
                        HttpClientBuilder.getClient(),
                        Util.getUserAgent(it, "RedditBrowser"),
                        DefaultBandwidthMeter()
                    )
                    mediaSource =
                        DashMediaSource.Factory(
                            DefaultDashChunkSource.Factory(dataSource),
                            dataSource
                        )
                            .createMediaSource(Uri.parse(content))
                }
            }
        }
        view.title_view.text = title
        view.subreddit_view.text = subreddit
        view.download_button.setOnClickListener { download(content!!) }
        return view
    }

    override fun onStart() {
        super.onStart()
        player?.playWhenReady = false
        player?.prepare(mediaSource)
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()
        player?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    private fun download(url: String) {
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permission =
                    it.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        42
                    )
                    pendingUrl = url
                    return
                }
            }
            Downloader.download(it, url)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(post: Post): VideoPostPage {
            return VideoPostPage().apply {
                arguments = Bundle().apply {
                    putInt("type", post.type)
                    putString("content", post.content)
                    putString("title", post.title)
                    putString("subreddit", post.subreddit)
                }
            }
        }
    }
}