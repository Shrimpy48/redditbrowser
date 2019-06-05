package com.example.redditbrowser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.image_card.view.*

class CardsAdapter(private val info: ArrayList<PostInfo>) : RecyclerView.Adapter<CardsAdapter.ViewHolder>(),
    PostTaskResponse {
    private lateinit var postfetcher: FetchPostTask

    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        postfetcher = FetchPostTask(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.image_card, parent, false) as CardView
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardView.image_card_title.text = info[position].title
        info[position].viewholder = holder
        postfetcher.execute(info[position])
    }

    override fun processFinish(output: ArrayList<PostData>) {
        for (data in output) {
            val viewholder = data.info.viewholder
            if (viewholder != null) viewholder.cardView.image_card_image.setImageURI(data.image)
        }
    }

    override fun getItemCount(): Int = info.size
}
