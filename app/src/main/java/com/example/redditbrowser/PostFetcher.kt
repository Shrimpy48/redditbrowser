package com.example.redditbrowser

import android.net.Uri
import java.net.URL

class PostFetcher {
    fun fetchPosts(info: FeedInfo): ArrayList<PostInfo> {
        // TODO
        return ArrayList()
    }

    fun fetchData(info: PostInfo): PostData {
        // TODO
        return PostData(PostInfo("", URL("")), "", Uri.EMPTY)
    }
}
