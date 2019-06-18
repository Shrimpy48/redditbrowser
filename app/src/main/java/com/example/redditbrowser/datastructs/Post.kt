package com.example.redditbrowser.datastructs

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    indices = [Index(value = ["subreddit"], unique = false)]
)
data class Post(
    @PrimaryKey
    val name: String,
    val title: String,
    val author: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val subreddit: String,
    val type: PostType,
    val url: Uri? = null,
    val selftext: String? = null,
    val width: Int? = null,
    val height: Int? = null
) {
    var feed = Feed("", -1)
}
