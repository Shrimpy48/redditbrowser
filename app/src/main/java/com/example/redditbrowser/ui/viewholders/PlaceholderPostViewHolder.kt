package com.example.redditbrowser.ui.viewholders

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R

class PlaceholderPostViewHolder(cardView: View) :
    RecyclerView.ViewHolder(cardView) {

    companion object {
        fun create(parent: ViewGroup): PlaceholderPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.placeholder_post, parent, false)
            return PlaceholderPostViewHolder(view)
        }
    }

    fun bind() {
        Log.d("PlaceholderPost", "Bound")
    }
}
