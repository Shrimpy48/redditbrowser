package com.example.redditbrowser.ui.pages

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.redditbrowser.R
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.web.Downloader
import com.example.redditbrowser.web.GlideApp
import kotlinx.android.synthetic.main.image_page.view.*

class ImagePostPage : Fragment() {

    private var content: String? = null

    private var pendingUrl = ""

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
        registerForContextMenu(view.image_view)
        return view
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.media_context, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.download_item -> {
                download(content!!)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun download(url: String) {
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permission =
                    it.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        42
                    )
                    pendingUrl = url
                    return
                }
            }
            Downloader.download(it, url)
        }
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