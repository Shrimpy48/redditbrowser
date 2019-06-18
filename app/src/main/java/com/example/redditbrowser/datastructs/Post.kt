package com.example.redditbrowser.datastructs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    indices = [Index(value = ["feed"], unique = false), Index(value = ["feedType"], unique = false)]
)
data class Post(
    @PrimaryKey
    val name: String,
    val title: String,
    val author: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val subreddit: String,
    val type: Int,
    val url: String? = null,
    val selftext: String? = null,
    val width: Int? = null,
    val height: Int? = null
) {
    var feed = ""
    var feedType = -1

    companion object {
        const val TEXT = 0
        const val IMAGE = 1
        const val VIDEO = 2
        const val DASH = 3
        const val URL = 4
    }
}
