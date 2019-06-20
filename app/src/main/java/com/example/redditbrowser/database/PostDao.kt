package com.example.redditbrowser.database

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.redditbrowser.datastructs.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<Post>)

    @Query("SELECT * FROM posts WHERE feed = :feed AND feedType = :feedType")
    fun postsByFeed(feed: String, feedType: Int): DataSource.Factory<Int, Post>

    @Query("DELETE FROM posts WHERE feed = :feed AND feedType = :feedType")
    fun deleteByFeed(feed: String, feedType: Int)
}
