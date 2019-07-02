package com.example.redditbrowser.datastructs

data class Feed(val feed: String, val feedType: Int) {
    companion object {
        const val TYPE_FRONTPAGE = 0
        const val TYPE_SUBREDDIT = 1
        const val TYPE_MULTIREDDIT = 2
    }
}
