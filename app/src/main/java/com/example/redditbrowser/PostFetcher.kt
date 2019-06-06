package com.example.redditbrowser

import android.net.Uri

class PostFetcher {
    fun fetchPosts(info: FeedInfo): ArrayList<PostInfo> {
        // TODO
        val out = ArrayList<PostInfo>()
        for (i in 1..5) out.add(PostInfo("test$i", null))
        return out
    }

    fun fetchData(info: PostInfo): PostData {
        // TODO
        return PostData(info, null, Uri.EMPTY)
    }
}
