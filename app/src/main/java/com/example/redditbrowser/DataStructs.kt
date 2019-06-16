package com.example.redditbrowser

import android.net.Uri


class ProcessedPost(new_title: String, new_type: PostType, new_body: String? = null, new_content_url: Uri? = null) {
    var title: String = new_title
    var type: PostType = new_type
    var body: String? = new_body
    var contentUrl: Uri? = new_content_url
}

enum class PostType {
    IMAGE, VIDEO, VIDEO_DASH, TEXT, URL
}

enum class FeedType {
    MULTI, SUBREDDIT, SPECIAL
}

class PostPage(var posts: List<ProcessedPost?>, var after: String?)
