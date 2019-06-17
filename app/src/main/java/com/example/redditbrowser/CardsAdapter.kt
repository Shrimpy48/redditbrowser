package com.example.redditbrowser

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.card.view.*

class CardsAdapter(
    private val context: Context,
    private val currentFeed: String,
    private val currentFeedType: FeedType
) :
    RecyclerView.Adapter<CardsAdapter.ViewHolder>() {

    private val info: ArrayList<ProcessedPost> = ArrayList()
    private var after: String? = null
    private var dontFetchMore: Boolean = false

    init {
        fetchMore()
    }

    class ViewHolder(val context: Context, val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        var player: SimpleExoPlayer? = null
        lateinit var data: ProcessedPost
        lateinit var source: MediaSource
        

        fun initPlayer() {
            player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
            cardView.card_video.player = player
            player!!.playWhenReady = true
            player!!.seekTo(0, 0)
            player!!.repeatMode = REPEAT_MODE_ONE
            cardView.card_video.controllerShowTimeoutMs = 1000
            cardView.card_video.hideController()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i("Adapter", "Creating holder")
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false) as CardView
        return ViewHolder(context, cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("Adapter", "Binding holder to $position")
        holder.data = info[position]
        holder.cardView.card_title.text = holder.data.title
        holder.cardView.card_subreddit.text = holder.data.subreddit

        when (holder.data.type) {
            PostType.IMAGE -> {
                holder.cardView.card_image.visibility = VISIBLE
                holder.cardView.card_media.visibility = VISIBLE
            }
            PostType.VIDEO -> {
                holder.cardView.card_video.visibility = VISIBLE
                holder.cardView.card_media.visibility = VISIBLE
                val dataSourceFactory = OkHttpDataSourceFactory(
                    HttpClientBuilder.getClient(), Util.getUserAgent(
                        context,
                        "RedditBrowser"
                    ), DefaultBandwidthMeter()
                )
                holder.source = ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(holder.data.contentUrl)
            }
            PostType.VIDEO_DASH -> {
                holder.cardView.card_video.visibility = VISIBLE
                holder.cardView.card_media.visibility = VISIBLE
                val dataSourceFactory = OkHttpDataSourceFactory(
                    HttpClientBuilder.getClient(), Util.getUserAgent(
                        context,
                        "RedditBrowser"
                    ), DefaultBandwidthMeter()
                )
                holder.source = DashMediaSource.Factory(
                    DefaultDashChunkSource.Factory(dataSourceFactory),
                    dataSourceFactory
                )
                    .createMediaSource(holder.data.contentUrl)
            }
            PostType.TEXT -> {
                holder.cardView.card_body.visibility = VISIBLE
                holder.cardView.card_body.text = holder.data.body
            }
            else -> {
                holder.cardView.card_body.visibility = VISIBLE
                holder.cardView.card_body.text = holder.data.contentUrl.toString()
            }
        }

        holder.cardView.card_media.setOnClickListener {
            val intent = Intent(context, FullscreenPostActivity::class.java)
            intent.action = ACTION_VIEW
            intent.data = holder.data.contentUrl
            val bundle = Bundle()
            bundle.putSerializable("type", holder.data.type)
            bundle.putString("title", holder.data.title)
            bundle.putString("subreddit", holder.data.subreddit)
            bundle.putString("body", holder.data.body)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        Log.i("Adapter", "Attaching holder to ${holder.adapterPosition}")
        super.onViewAttachedToWindow(holder)

        when {
            holder.data.type == PostType.IMAGE -> {
                GlideApp.with(context)
                    .load(holder.data.contentUrl)
                    .thumbnail(0.1f)
                    .into(holder.cardView.card_image)
            }
            holder.data.type == PostType.VIDEO -> {
                holder.initPlayer()
                holder.player!!.prepare(holder.source, true, false)
                holder.player!!.volume = 0f
            }
            holder.data.type == PostType.VIDEO_DASH -> {
                holder.initPlayer()
                holder.player!!.prepare(holder.source, true, false)
                holder.player!!.volume = 0f
            }
        }

        if (!dontFetchMore && info.size - holder.adapterPosition < 6) {
            fetchMore()
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        Log.i("Adapter", "Detaching holder from ${holder.adapterPosition}")
        super.onViewDetachedFromWindow(holder)
        GlideApp.with(context)
            .clear(holder.cardView.card_image)
        if (holder.player != null)
            holder.player!!.release()
    }

    override fun onViewRecycled(holder: ViewHolder) {
        Log.i("Adapter", "Recycling holder")
        super.onViewRecycled(holder)
        holder.cardView.card_image.visibility = GONE
        holder.cardView.card_video.visibility = GONE
        holder.cardView.card_body.visibility = GONE
        holder.cardView.card_media.visibility = GONE
    }

    override fun getItemCount(): Int = info.size

    private fun fetchMore() {
        Log.i("Adapter", "Fetching content")
        dontFetchMore = true
        if (currentFeedType == FeedType.SPECIAL && currentFeed == "Front page")
            ApiFetcher.getMyFrontPagePosts(after, info.size, object : ApiFetcher.Listener<PostPage?> {
                override fun onComplete(result: PostPage?) {
                    if (result != null) {
                        var count = 0
                        val start = info.size
                        for (post in result.posts)
                            if (post != null) {
                                info.add(post)
                                count++
                            }
                        notifyItemRangeInserted(start, count)
                        after = result.after
                        if (after != null)
                            dontFetchMore = false
                    }
                }
            })
        else if (currentFeedType == FeedType.SUBREDDIT)
            ApiFetcher.getSubredditPosts(currentFeed, after, info.size, object : ApiFetcher.Listener<PostPage?> {
                override fun onComplete(result: PostPage?) {
                    if (result != null) {
                        var count = 0
                        val start = info.size
                        for (post in result.posts)
                            if (post != null) {
                                info.add(post)
                                count++
                            }
                        notifyItemRangeInserted(start, count)
                        after = result.after
                        if (after != null)
                            dontFetchMore = false
                    }
                }
            })
        else if (currentFeedType == FeedType.MULTI)
            ApiFetcher.getMyMultiPosts(currentFeed, after, info.size, object : ApiFetcher.Listener<PostPage?> {
                override fun onComplete(result: PostPage?) {
                    if (result != null) {
                        var count = 0
                        val start = info.size
                        for (post in result.posts)
                            if (post != null) {
                                info.add(post)
                                count++
                            }
                        notifyItemRangeInserted(start, count)
                        after = result.after
                        if (after != null)
                            dontFetchMore = false
                    }
                }
            })
        else {
            dontFetchMore = false
            Log.w("Feed", "Unknown feed: $currentFeed (type $currentFeedType)")
        }
    }
}
