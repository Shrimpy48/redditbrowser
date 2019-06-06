package com.example.redditbrowser

import android.net.Uri
import java.net.URL

class FeedInfo

class PostInfo(new_title: String, new_content_url: URL?) {
    var viewholder: CardsAdapter.ViewHolder? = null
    var title: String = new_title
    var content_url: URL? = new_content_url
}

class PostData(new_info: PostInfo, new_body: String?, new_image: Uri) {
    var info: PostInfo = new_info
    var body: String? = new_body
    var image: Uri = new_image
}
