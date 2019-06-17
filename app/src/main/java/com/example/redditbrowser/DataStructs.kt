package com.example.redditbrowser

import android.net.Uri


class ProcessedPost(
    var title: String,
    var type: PostType,
    var subreddit: String,
    var body: String? = null,
    var contentUrl: Uri? = null,
    var width: Int? = null,
    var height: Int? = null
)

enum class PostType {
    IMAGE, VIDEO, VIDEO_DASH, TEXT, URL
}

enum class FeedType {
    MULTI, SUBREDDIT, SPECIAL
}

class PostPage(var posts: List<ProcessedPost?>, var after: String?)
