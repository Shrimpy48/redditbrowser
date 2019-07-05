package com.example.redditbrowser.datastructs

import androidx.room.Entity

@Entity(primaryKeys = ["name", "feed", "feedType", "sort", "period"])
data class Post(
    val name: String,
    val id: String,
    val title: String,
    val author: String,
    val subreddit: String,
    val nsfw: Boolean,
    val spoiler: Boolean,
    val type: Int,
    val score: Int,
    val contentUrl: String? = null,
    val postUrl: String? = null,
    val selftext: String? = null,
    val width: Int? = null,
    val height: Int? = null
) {
    var feed = ""
    var feedType = -1
    var sort: String = ""
    var period: String = ""

    companion object {
        const val TEXT = 0
        const val IMAGE = 1
        const val VIDEO = 2
        const val DASH = 3
        const val EMBED = 4
        const val URL = 5
    }
}
