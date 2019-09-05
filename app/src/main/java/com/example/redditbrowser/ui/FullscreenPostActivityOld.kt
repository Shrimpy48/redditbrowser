package com.example.redditbrowser.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.web.Downloader
import com.example.redditbrowser.web.GlideApp
import com.example.redditbrowser.web.HttpClientBuilder
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_fullscreen_post_image.*
import kotlinx.android.synthetic.main.activity_fullscreen_post_text.*
import kotlinx.android.synthetic.main.activity_fullscreen_post_video.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenPostActivityOld : AppCompatActivity() {

    private var type = -1

    private var pendingUrl = ""

//    private val mHideHandler = Handler()
//    private val mHidePart2Runnable = Runnable {
//        // Delayed removal of status and navigation bar
//
//        // Note that some of these constants are new as of API 16 (Jelly Bean)
//        // and API 19 (KitKat). It is safe to use them, as they are inlined
//        // at compile-time and do nothing on earlier devices.
//        val flags =
//            View.SYSTEM_UI_FLAG_LOW_PROFILE or
//                    View.SYSTEM_UI_FLAG_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//
//        when (type) {
//            Post.TEXT -> fullscreen_text.systemUiVisibility = flags
//            Post.IMAGE -> fullscreen_image.systemUiVisibility = flags
//            Post.VIDEO -> fullscreen_video.systemUiVisibility = flags
//            Post.VIDEO_DASH -> fullscreen_video.systemUiVisibility = flags
//            else -> throw IllegalArgumentException("Invalid post type")
//        }
//    }
//    private val mShowPart2Runnable = Runnable {
//        // Delayed display of UI elements
//        supportActionBar?.show()
//    }
//    private var mVisible: Boolean = false
//    private val mHideRunnable = Runnable { hide() }

    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = intent.getIntExtra("type", -1)

        when (type) {
            Post.TEXT -> setContentView(R.layout.activity_fullscreen_post_text)
            Post.IMAGE -> setContentView(R.layout.activity_fullscreen_post_image)
            Post.VIDEO -> setContentView(R.layout.activity_fullscreen_post_video)
            Post.VIDEO_DASH -> setContentView(R.layout.activity_fullscreen_post_video)
            else -> throw IllegalArgumentException("Invalid post type")
        }

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
//        when (type) {
//            Post.TEXT -> fullscreen_text.setOnClickListener { toggle() }
//            Post.IMAGE -> fullscreen_image.setOnClickListener { toggle() }
//            Post.VIDEO -> fullscreen_video.setOnClickListener { toggle() }
//            Post.VIDEO_DASH -> fullscreen_video.setOnClickListener { toggle() }
//            else -> throw IllegalArgumentException("Invalid post type")
//        }

        when (type) {
            Post.TEXT -> {
                fullscreen_text.text = intent.getStringExtra("selftext")
            }
            Post.IMAGE -> {
                GlideApp.with(this)
                    .load(intent.getStringExtra("url"))
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(fullscreen_image)
                fullscreen_dl_image.setOnClickListener {
                    download(intent.getStringExtra("url"))
                }
            }
            Post.VIDEO -> {
                player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
                fullscreen_video.player = player
                player!!.playWhenReady = true
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val useOkHttpExoPlayer = prefs.getBoolean("useOkHttpExoPlayer", true)
                val dataSource = if (useOkHttpExoPlayer) OkHttpDataSourceFactory(
                    HttpClientBuilder.getClient(),
                    Util.getUserAgent(this, "RedditBrowser"),
                    DefaultBandwidthMeter()
                ) else DefaultDataSourceFactory(
                    this,
                    Util.getUserAgent(this, "RedditBrowser")
                )
                val mediaSource = ExtractorMediaSource.Factory(dataSource)
                    .createMediaSource(Uri.parse(intent.getStringExtra("url")))
                player!!.prepare(mediaSource)
                fullscreen_dl_video.setOnClickListener {
                    download(intent.getStringExtra("url"))
                }
            }
            Post.VIDEO_DASH -> {
                player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
                fullscreen_video.player = player
                player!!.playWhenReady = true
                val dataSource = OkHttpDataSourceFactory(
                    HttpClientBuilder.getClient(),
                    Util.getUserAgent(this, "RedditBrowser"),
                    DefaultBandwidthMeter()
                )
                val mediaSource = DashMediaSource.Factory(DefaultDashChunkSource.Factory(dataSource), dataSource)
                    .createMediaSource(Uri.parse(intent.getStringExtra("url")))
                player!!.prepare(mediaSource)
                fullscreen_dl_video.setOnClickListener {
                    Toast.makeText(this, "Cannot download DASH video", Toast.LENGTH_SHORT).show()
                }
            }
            else -> throw IllegalArgumentException("Invalid post type")
        }
    }

//    override fun onPostCreate(savedInstanceState: Bundle?) {
//        super.onPostCreate(savedInstanceState)
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        delayedHide(100)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        if (id == android.R.id.home) {
//            // This ID represents the Home or Up button.
//            NavUtils.navigateUpFromSameTask(this)
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onDestroy() {
        super.onDestroy()
        if (type == Post.VIDEO || type == Post.VIDEO_DASH) {
            if (player != null) player!!.release()
        }
    }

    private fun download(url: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission =
                this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    42
                )
                pendingUrl = url
                return
            }
        }
        Downloader.download(this, url)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 42 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            download(pendingUrl)
        }
    }

//    private fun toggle() {
//        if (mVisible) {
//            hide()
//        } else {
//            show()
//        }
//    }
//
//    private fun hide() {
//        // Hide UI first
//        supportActionBar?.hide()
//        mVisible = false
//
//        // Schedule a runnable to remove the status and navigation bar after a delay
//        mHideHandler.removeCallbacks(mShowPart2Runnable)
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
//    }
//
//    private fun show() {
//        // Show the system bar
//        val flags =
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        when (type) {
//            Post.TEXT -> fullscreen_text.systemUiVisibility = flags
//            Post.IMAGE -> fullscreen_image.systemUiVisibility = flags
//            Post.VIDEO -> fullscreen_video.systemUiVisibility = flags
//            Post.VIDEO_DASH -> fullscreen_video.systemUiVisibility = flags
//            else -> throw IllegalArgumentException("Invalid post type")
//        }
//        mVisible = true
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable)
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
//    }
//
//    /**
//     * Schedules a call to hide() in [delayMillis], canceling any
//     * previously scheduled calls.
//     */
//    private fun delayedHide(delayMillis: Int) {
//        mHideHandler.removeCallbacks(mHideRunnable)
//        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
//    }
//
//    companion object {
//        /**
//         * Whether or not the system UI should be auto-hidden after
//         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
//         */
//        private const val AUTO_HIDE = true
//
//        /**
//         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
//         * user interaction before hiding the system UI.
//         */
//        private const val AUTO_HIDE_DELAY_MILLIS = 3000
//
//        /**
//         * Some older devices needs a small delay between UI widget updates
//         * and a change of the status and navigation bar.
//         */
//        private const val UI_ANIMATION_DELAY = 300
//    }
}