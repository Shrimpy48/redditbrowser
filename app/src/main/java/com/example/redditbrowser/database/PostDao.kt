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

    @Query("SELECT * FROM Post WHERE feed = :feed AND feedType = :feedType ORDER BY position")
    fun postsByFeed(feed: String, feedType: Int): DataSource.Factory<Int, Post>

    @Query("SELECT * FROM Post WHERE feed = :feed AND feedType = :feedType AND sort = :sort ORDER BY position")
    fun postsByFeed(feed: String, feedType: Int, sort: String): DataSource.Factory<Int, Post>

    @Query("SELECT * FROM Post WHERE feed = :feed AND feedType = :feedType AND sort = :sort AND period = :period ORDER BY position")
    fun postsByFeed(feed: String, feedType: Int, sort: String, period: String): DataSource.Factory<Int, Post>

    @Query("DELETE FROM Post WHERE feed = :feed AND feedType = :feedType")
    fun deleteByFeed(feed: String, feedType: Int)

    @Query("DELETE FROM Post WHERE feed = :feed AND feedType = :feedType AND sort = :sort")
    fun deleteByFeed(feed: String, feedType: Int, sort: String)

    @Query("DELETE FROM Post WHERE feed = :feed AND feedType = :feedType AND sort = :sort AND period = :period")
    fun deleteByFeed(feed: String, feedType: Int, sort: String, period: String)
}
