package com.example.redditbrowser.ui.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.web.GlideApp
import kotlinx.android.synthetic.main.image_page.view.*

class ImagePostPage : Fragment() {

    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            content = it.getString("content")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.image_page, container, false)
        GlideApp.with(this)
            .load(content)
            .placeholder(R.drawable.ic_image_black_24dp)
            .error(R.drawable.ic_error_black_24dp)
            .into(view.image_view)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(post: Post): ImagePostPage {
            return ImagePostPage().apply {
                arguments = Bundle().apply {
                    putString("content", post.content)
                }
            }
        }
    }
}