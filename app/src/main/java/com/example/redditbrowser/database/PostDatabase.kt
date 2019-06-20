package com.example.redditbrowser.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.redditbrowser.datastructs.Post


@Database(
    entities = [Post::class],
    version = 2,
    exportSchema = false
)
abstract class PostDatabase : RoomDatabase() {
    companion object {
        fun create(context: Context, inMemory: Boolean): PostDatabase {
            val dbBuilder = if (inMemory) Room.inMemoryDatabaseBuilder(context, PostDatabase::class.java)
            else Room.databaseBuilder(context, PostDatabase::class.java, "posts.db")
            return dbBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun posts(): PostDao
}
