package com.example.redditbrowser.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.toLiveData
import com.example.redditbrowser.apis.ApiFetcher
import com.example.redditbrowser.database.PostDatabase
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.datastructs.Listing
import com.example.redditbrowser.datastructs.NetworkState
import com.example.redditbrowser.datastructs.Post
import java.util.concurrent.Executor

class PostRepository(
    val db: PostDatabase,
    private val executor: Executor
) {

    private fun insertResultIntoDb(feed: Feed, res: List<Post>) {
        db.runInTransaction {
            db.posts().insert(res.map { post ->
                post.feed = feed.feed
                post.feedType = feed.feedType
                post
            })
        }
    }

    @MainThread
    private fun refresh(feed: Feed): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        ApiFetcher.getFeedPosts(feed, object : ApiFetcher.Listener<List<Post>> {
            override fun onComplete(result: List<Post>) {
                executor.execute {
                    db.runInTransaction {
                        db.posts().deleteByFeed(feed.feed, feed.feedType)
                        insertResultIntoDb(feed, result)
                    }

                    networkState.postValue(NetworkState.LOADED)
                }
            }
        })
        return networkState
    }

    fun postsOfFeed(feed: Feed, pageSize: Int): Listing<Post> {
        val callback = BoundaryCallback(feed, this::insertResultIntoDb, executor)

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = switchMap(refreshTrigger) {
            refresh(feed)
        }

        val livePagedList = db.posts().postsByFeed(feed.feed, feed.feedType).toLiveData(
            pageSize = pageSize, boundaryCallback = callback
        )

        return Listing(
            pagedList = livePagedList,
            networkState = callback.networkState,
            retry = { callback.helper.retryAllFailed() },
            refresh = { refreshTrigger.value = null },
            refreshState = refreshState
        )
    }

}
