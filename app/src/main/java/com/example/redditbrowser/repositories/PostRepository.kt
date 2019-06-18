package com.example.redditbrowser.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.datastructs.Listing
import com.example.redditbrowser.datastructs.NetworkState
import com.example.redditbrowser.datastructs.Post

class PostRepository {

    private fun insertResultIntoDb() {

    }

    @MainThread
    private fun refresh(feed: Feed): LiveData<NetworkState> {

    }

    fun postsOfFeed(feed: Feed, pageSize: Int): Listing<Post> {

    }

}