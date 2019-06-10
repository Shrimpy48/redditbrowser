package com.example.redditbrowser

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class CardSpacer(private val spaceHeight: Int, private val spaceWidth: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = spaceHeight
        val col = (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).spanIndex
        if (col == 0) outRect.left = spaceWidth
        outRect.right = spaceWidth
    }
}
