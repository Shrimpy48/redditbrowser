package com.example.redditbrowser

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
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.card.view.*

class CardsAdapter(private val apiServiceOauth: RedditApiService, private val tokenResp: Single<AuthResponse>) :
    RecyclerView.Adapter<CardsAdapter.ViewHolder>() {

    private val info: ArrayList<ProcessedPost> = ArrayList()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var after: String? = null
    private var isFetching: Boolean = false

    init {
        fetchMore()
    }

    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        var player: SimpleExoPlayer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false) as CardView
        val holder = ViewHolder(cardView)
        holder.player = null
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardView.card_title.text = info[position].title
        when {
            info[position].type == PostType.IMAGE -> {
                holder.cardView.card_image.visibility = VISIBLE
                Glide.with(holder.cardView.card_image.context)
                    .load(info[position].contentUrl)
                    .into(holder.cardView.card_image)
            }
            info[position].type == PostType.VIDEO -> {
                holder.cardView.card_video.visibility = VISIBLE
                holder.player = ExoPlayerFactory.newSimpleInstance(
                    DefaultRenderersFactory(holder.cardView.card_video.context),
                    DefaultTrackSelector(), DefaultLoadControl()
                )
                holder.cardView.card_video.player = holder.player
                holder.player?.playWhenReady = true
                holder.player?.seekTo(0, 0)
                holder.player?.repeatMode = REPEAT_MODE_ONE
                holder.cardView.card_video.controllerShowTimeoutMs = 1000
                holder.cardView.card_video.hideController()
                val mediaSource = ExtractorMediaSource.Factory(
                    DefaultHttpDataSourceFactory(
                        Util.getUserAgent(
                            holder.cardView.card_video.context,
                            "RedditBrowser"
                        )
                    )
                )
                    .createMediaSource(info[position].contentUrl)
                holder.player?.prepare(mediaSource, true, false)
            }
            info[position].type == PostType.VIDEO_DASH -> {
                holder.cardView.card_video.visibility = VISIBLE
                holder.player = ExoPlayerFactory.newSimpleInstance(
                    DefaultRenderersFactory(holder.cardView.card_video.context),
                    DefaultTrackSelector(), DefaultLoadControl()
                )
                holder.cardView.card_video.player = holder.player
                holder.player?.playWhenReady = true
                holder.player?.seekTo(0, 0)
                holder.player?.repeatMode = REPEAT_MODE_ONE
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
                holder.player?.prepare(mediaSource, true, false)
            }
            info[position].type == PostType.TEXT -> {
                holder.cardView.card_body.visibility = VISIBLE
                holder.cardView.card_body.text = info[position].body
            }
            else -> {
                holder.cardView.card_body.visibility = VISIBLE
                holder.cardView.card_body.text = info[position].contentUrl.toString()
            }
        }

        if (info.size - position < 5 && !isFetching) {
            fetchMore()
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.cardView.card_image.visibility = GONE
        holder.cardView.card_video.visibility = GONE
        holder.cardView.card_body.visibility = GONE
        Glide.with(holder.cardView.card_image.context)
            .clear(holder.cardView.card_image)
        if (holder.player != null) {
            holder.player?.release()
            holder.player = null
        }
    }

    override fun getItemCount(): Int = info.size

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }

    private fun fetchMore() {
        isFetching = true
        tokenResp.flatMap { firstResponse ->
            apiServiceOauth.getMyFrontPage(
                firstResponse.tokenType + " " + firstResponse.accessToken,
                after,
                info.size
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<PostInfoListWrapper> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(resp: PostInfoListWrapper) {
                    val start = info.size
                    var count = 0
                    val posts = resp.data?.children
                    val numPosts = resp.data?.dist
                    if (posts != null && numPosts != null)
                        for (i in 0 until numPosts) {
                            val parsed = RedditFetcher.ParsePost(posts[i].data!!)
                            if (parsed != null) {
                                info.add(parsed)
                                count++
                            }
                        }
                    after = resp.data?.after
                    notifyItemRangeInserted(start, count)
                    isFetching = false
                }

                override fun onError(e: Throwable) {
                    Log.e("Feed", e.localizedMessage)
                }
            })
    }
}
