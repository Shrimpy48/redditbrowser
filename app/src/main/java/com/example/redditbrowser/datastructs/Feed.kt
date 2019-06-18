package com.example.redditbrowser.datastructs

class Feed(val feed: String, val feedType: Int) {
    companion object {
        const val TYPE_SPECIAL = 0
        const val TYPE_SUBREDDIT = 1
        const val TYPE_MULTIREDDIT = 2
    }
}
