package com.example.redditbrowser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.image_card.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class CardsAdapter(private val data: Array<PostData>) : RecyclerView.Adapter<CardsAdapter.ViewHolder>() {

    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.image_card, parent, false) as CardView
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardView.imageView.setImageURI(data[position].image)
        holder.cardView.image_card_title.text = data[position].title
    }

    override fun getItemCount(): Int = data.size
}
