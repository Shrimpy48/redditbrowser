package com.example.redditbrowser

import android.net.Uri


object RedditFetcher {

    fun ParsePost(info: PostInfo): ProcessedPost? {
        val title = info.title
        var contentUri: Uri? = null
        val type: PostType
        var body: String? = null
        if (info.isSelf != null && info.isSelf!!) {
            body = info.selftext
            type = PostType.TEXT
        } else if (info.secureMedia != null && info.secureMedia?.redditVideo != null) {
            contentUri = Uri.parse(info.secureMedia?.redditVideo?.dashUrl)
            type = PostType.VIDEO_DASH
        } else if (info.preview != null && info.preview?.redditVideoPreview != null) {
            contentUri = Uri.parse(info.preview?.redditVideoPreview?.dashUrl)
            type = PostType.VIDEO_DASH
        } else if (info.postHint == "image") {
            contentUri = Uri.parse(info.url)
            type = PostType.IMAGE
        } else {
            contentUri = Uri.parse(info.url)
            type = PostType.URL
        }
        if (title != null)
            return ProcessedPost(title, type, body, contentUri)
        return null
    }

}
