package com.example.redditbrowser

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
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.card.view.*

class CardsAdapter(private val currentFeed: String, private val currentFeedType: FeedType) :
    RecyclerView.Adapter<CardsAdapter.ViewHolder>() {

    private val info: ArrayList<ProcessedPost> = ArrayList()
    private var after: String? = null
    private var dontFetchMore: Boolean = false

    init {
        fetchMore()
    }

    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        lateinit var player: SimpleExoPlayer

        fun initPlayer() {
            player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(cardView.card_video.context),
                DefaultTrackSelector(), DefaultLoadControl()
            )
            cardView.card_video.player = player
            player.playWhenReady = true
            player.seekTo(0, 0)
            player.repeatMode = REPEAT_MODE_ONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false) as CardView
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initPlayer()
        holder.cardView.card_title.text = info[position].title
        when (info[position].type) {
            PostType.IMAGE -> {
                holder.cardView.card_image.visibility = VISIBLE
                holder.cardView.card_media.visibility = VISIBLE
                Glide.with(holder.cardView.card_image.context)
                    .load(info[position].contentUrl)
                    .into(holder.cardView.card_image)

                holder.cardView.card_media.setOnClickListener {
                    val intent = Intent(holder.cardView.card_media.context, FullscreenPostActivity::class.java)
                    intent.action = ACTION_VIEW
                    intent.data = info[position].contentUrl
                    val bundle = Bundle()
                    bundle.putSerializable("type", info[position].type)
                    bundle.putString("title", info[position].title)
                    bundle.putString("body", info[position].body)
                    intent.putExtras(bundle)
                    holder.cardView.card_media.context.startActivity(intent)
                }
            }
            PostType.VIDEO -> {
                holder.cardView.card_video.visibility = VISIBLE
                holder.cardView.card_media.visibility = VISIBLE

                val mediaSource = ExtractorMediaSource.Factory(
                    DefaultHttpDataSourceFactory(
                        Util.getUserAgent(
                            holder.cardView.card_video.context,
                            "RedditBrowser"
                        )
                    )
                )
                    .createMediaSource(info[position].contentUrl)
                holder.player.prepare(mediaSource, true, false)
                holder.player.volume = 0f

                holder.cardView.card_media.setOnClickListener {
                    val intent = Intent(holder.cardView.card_media.context, FullscreenPostActivity::class.java)
                    intent.action = ACTION_VIEW
                    intent.data = info[position].contentUrl
                    val bundle = Bundle()
                    bundle.putSerializable("type", info[position].type)
                    bundle.putString("title", info[position].title)
                    intent.putExtras(bundle)
                    holder.cardView.card_media.context.startActivity(intent)
                }
            }
            PostType.VIDEO_DASH -> {
                holder.cardView.card_video.visibility = VISIBLE
                holder.cardView.card_media.visibility = VISIBLE
                holder.player = ExoPlayerFactory.newSimpleInstance(
                    DefaultRenderersFactory(holder.cardView.card_video.context),
                    DefaultTrackSelector(), DefaultLoadControl()
                )
                holder.cardView.card_video.player = holder.player
                holder.player.playWhenReady = true
                holder.player.seekTo(0, 0)
                holder.player.repeatMode = REPEAT_MODE_ONE
                holder.cardView.card_video.controllerShowTimeoutMs = 1000
                holder.cardView.card_video.hideController()
                val dataSourceFactory = DefaultHttpDataSourceFactory(
                    Util.getUserAgent(
                        holder.cardView.card_video.context,
                        "RedditBrowser"
                    )
                ) as DataSource.Factory
                val mediaSource =
                    DashMediaSource.Factory(DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory)
                        .createMediaSource(info[position].contentUrl)
                holder.player.prepare(mediaSource, true, false)
                holder.player.volume = 0f

                holder.cardView.card_media.setOnClickListener {
                    val intent = Intent(holder.cardView.card_media.context, FullscreenPostActivity::class.java)
                    intent.action = ACTION_VIEW
                    intent.data = info[position].contentUrl
                    val bundle = Bundle()
                    bundle.putSerializable("type", info[position].type)
                    bundle.putString("title", info[position].title)
                    intent.putExtras(bundle)
                    holder.cardView.card_media.context.startActivity(intent)
                }
            }
            PostType.TEXT -> {
                holder.cardView.card_body.visibility = VISIBLE
                holder.cardView.card_body.text = info[position].body
            }
            else -> {
                holder.cardView.card_body.visibility = VISIBLE
                holder.cardView.card_body.text = info[position].contentUrl.toString()
            }
        }

        if (info.size - position < 5 && !dontFetchMore) {
            fetchMore()
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.cardView.card_image.visibility = GONE
        holder.cardView.card_video.visibility = GONE
        holder.cardView.card_body.visibility = GONE
        holder.cardView.card_media.visibility = GONE
        Glide.with(holder.cardView.card_image.context)
            .clear(holder.cardView.card_image)
        holder.player.release()
    }

    override fun getItemCount(): Int = info.size

    private fun fetchMore() {
        dontFetchMore = true
        if (currentFeedType == FeedType.SPECIAL && currentFeed == "Front page")
            RedditFetcher.getMyFrontPagePosts(after, info.size, object : RedditFetcher.Listener<PostPage?> {
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
            RedditFetcher.getSubredditPosts(currentFeed, after, info.size, object : RedditFetcher.Listener<PostPage?> {
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
            RedditFetcher.getMyMultiPosts(currentFeed, after, info.size, object : RedditFetcher.Listener<PostPage?> {
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
