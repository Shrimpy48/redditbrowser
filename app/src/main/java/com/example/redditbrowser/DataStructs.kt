package com.example.redditbrowser

import android.net.Uri

class FeedInfo

class PostInfo(new_title: String, new_type: PostType, new_body: String? = null, new_content_url: Uri? = null) {
    var title: String = new_title
    var type: PostType = new_type
    var body: String? = new_body
    var contenturl: Uri? = new_content_url
}

enum class PostType {
    IMAGE, VIDEO, TEXT, URL
}
