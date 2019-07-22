package com.example.redditbrowser.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.redditbrowser.R
import kotlinx.android.synthetic.main.nav_list_item.view.*


class MultisAdapter(private val selectedCallback: (String) -> Unit) :
    ListAdapter<String, MultisAdapter.ViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<String>() {
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }

    class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        val textView: TextView = parent.nav_item_text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.nav_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val multi = getItem(position)
        holder.textView.text = multi
        holder.textView.setOnClickListener {
            selectedCallback(multi)
        }
    }
}
