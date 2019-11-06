package com.example.redditbrowser.ui

import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.paging.PagedList
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.ui.pages.*

class PostPagerAdapter(fragmentManager: FragmentManager, private val useWebView: Boolean) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var list: PagedList<Post>? = null

    override fun getItem(position: Int): Fragment {
        list?.loadAround(position)
        val post = list?.get(position)
        return when (post?.type) {
            Post.TEXT -> TextPostPage.newInstance(post)
            Post.IMAGE -> ImagePostPage.newInstance(post)
            Post.VIDEO -> VideoPostPage.newInstance(post)
            Post.VIDEO_DASH -> VideoPostPage.newInstance(post)
            Post.EMBED -> if (useWebView) EmbedPostPage.newInstance(post) else UrlPostPage.newInstance(
                post
            )
            Post.EMBED_HTML -> if (useWebView) EmbedPostPage.newInstance(post) else UrlPostPage.newInstance(
                post
            )
            Post.URL -> UrlPostPage.newInstance(post)
            null -> PlaceholderPostPage()
            else -> throw IllegalArgumentException("unknown view type ${post.type}")
        }
    }

    fun submitList(newList: PagedList<Post>) {
        Log.d("Pager", "new list")
        list = newList
        notifyDataSetChanged()
    }

    override fun getCount(): Int = list?.size ?: 0

    override fun saveState(): Parcelable? {
        return null
    }

}
