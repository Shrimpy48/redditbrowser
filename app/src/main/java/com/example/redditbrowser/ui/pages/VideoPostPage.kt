package com.example.redditbrowser.ui.pages

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.web.HttpClientBuilder
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.video_page.view.*

class VideoPostPage : Fragment() {

    private var type: Int = -1
    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt("type")
            content = it.getString("content")
        }
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
                    val player = ExoPlayerFactory.newSimpleInstance(it, DefaultTrackSelector())
                    view.video_view.player = player
                    player.playWhenReady = true
                    val prefs = PreferenceManager.getDefaultSharedPreferences(it)
                    val useOkHttpExoPlayer = prefs.getBoolean("useOkHttpExoPlayer", true)
                    val dataSource = if (useOkHttpExoPlayer) OkHttpDataSourceFactory(
                        HttpClientBuilder.getClient(),
                        Util.getUserAgent(it, "RedditBrowser"),
                        DefaultBandwidthMeter()
                    ) else DefaultDataSourceFactory(
                        activity,
                        Util.getUserAgent(activity, "RedditBrowser")
                    )
                    val mediaSource = ExtractorMediaSource.Factory(dataSource)
                        .createMediaSource(Uri.parse(content))
                    player.prepare(mediaSource)
                }
            }
            Post.VIDEO_DASH -> {
                activity?.let {
                    val player = ExoPlayerFactory.newSimpleInstance(it, DefaultTrackSelector())
                    view.video_view.player = player
                    player.playWhenReady = true
                    val dataSource = OkHttpDataSourceFactory(
                        HttpClientBuilder.getClient(),
                        Util.getUserAgent(it, "RedditBrowser"),
                        DefaultBandwidthMeter()
                    )
                    val mediaSource =
                        DashMediaSource.Factory(
                            DefaultDashChunkSource.Factory(dataSource),
                            dataSource
                        )
                            .createMediaSource(Uri.parse(content))
                    player.prepare(mediaSource)
                }
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(post: Post): VideoPostPage {
            return VideoPostPage().apply {
                arguments = Bundle().apply {
                    putInt("type", post.type)
                    putString("content", post.content)
                }
            }
        }
    }
}