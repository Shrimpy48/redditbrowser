package com.example.redditbrowser.ui.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post

class TextPostViewHolder(cardView: View) : RecyclerView.ViewHolder(cardView) {

    companion object {
        fun create(parent: ViewGroup): TextPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.url_post, parent, false)
            return TextPostViewHolder(view)
        }
    }

    fun bind(post: Post?) {

    }
}
