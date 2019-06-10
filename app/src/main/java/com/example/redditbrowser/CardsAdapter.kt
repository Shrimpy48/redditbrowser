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
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.card.view.*

class CardsAdapter(private val info: ArrayList<PostInfo>) : RecyclerView.Adapter<CardsAdapter.ViewHolder>() {

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
                holder.cardView.card_video.visibility = GONE
                holder.cardView.card_body.visibility = GONE
                Glide.with(holder.cardView.card_image.context)
                    .load(info[position].contenturl)
                    .into(holder.cardView.card_image)
                if (holder.player != null) {
                    holder.player?.release()
                    holder.player = null
                }
            }
            info[position].type == PostType.VIDEO -> {
                holder.cardView.card_image.visibility = GONE
                holder.cardView.card_video.visibility = VISIBLE
                holder.cardView.card_body.visibility = GONE
                Glide.with(holder.cardView.card_image.context)
                    .clear(holder.cardView.card_image)
                Log.i("Video", "URL: ${info[position].contenturl}")
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
                val mediaSource = ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("redditbrowser"))
                    .createMediaSource(info[position].contenturl)
                holder.player?.prepare(mediaSource, true, false)
            }
            info[position].type == PostType.TEXT -> {
                holder.cardView.card_image.visibility = GONE
                holder.cardView.card_video.visibility = GONE
                holder.cardView.card_body.visibility = VISIBLE
                Glide.with(holder.cardView.card_image.context)
                    .clear(holder.cardView.card_image)
                if (holder.player != null) {
                    holder.player?.release()
                    holder.player = null
                }
                holder.cardView.card_body.text = info[position].body
            }
            else -> {
                holder.cardView.card_image.visibility = GONE
                holder.cardView.card_video.visibility = GONE
                holder.cardView.card_body.visibility = VISIBLE
                Glide.with(holder.cardView.card_image.context)
                    .clear(holder.cardView.card_image)
                if (holder.player != null) {
                    holder.player?.release()
                    holder.player = null
                }
                holder.cardView.card_body.text = info[position].contenturl.toString()
            }
        }
    }

    override fun getItemCount(): Int = info.size
}
