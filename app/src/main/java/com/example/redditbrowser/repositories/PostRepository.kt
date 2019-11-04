package com.example.redditbrowser.repositories

import android.util.Log
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

    private val networkPageSize = 10

    private fun insertResultIntoDb(feed: Feed, res: List<Post>, startpos: Int) {
        db.runInTransaction {
            db.posts().insert(res.mapIndexed { pos, post ->
                post.position = startpos + pos
                post.feed = feed.feed
                post.feedType = feed.feedType
                post.sort = feed.sort
                post.period = feed.period
                post
            })
        }
    }

    @MainThread
    private fun refresh(feed: Feed): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        ApiFetcher.getFeedPosts(feed, networkPageSize, object : ApiFetcher.Listener<ApiFetcher.Page<Post>> {
            override fun onComplete(result: ApiFetcher.Page<Post>) {
                executor.execute {
                    db.runInTransaction {
                        db.posts().deleteByFeed(feed.feed, feed.feedType, feed.sort, feed.period)
                        insertResultIntoDb(feed, result.items, 0)
                    }

                    networkState.postValue(NetworkState.LOADED)
                }
            }

            override fun onFailure(t: Throwable) {
                Log.e("Refresh", "" + t.localizedMessage)
                networkState.postValue(NetworkState.FAILED)
            }
        })
        return networkState
    }

    fun postsOfFeed(feed: Feed, pageSize: Int): Listing<Post> {
        val callback = BoundaryCallback(feed, networkPageSize, this::insertResultIntoDb, executor)

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = switchMap(refreshTrigger) {
            refresh(feed)
        }

        val livePagedList = db.posts().postsByFeed(feed.feed, feed.feedType, feed.sort, feed.period).toLiveData(
            pageSize = pageSize, boundaryCallback = callback
        )

        return Listing(
            pagedList = livePagedList,
            refresh = { refreshTrigger.value = null },
            refreshState = refreshState
        )
    }

}
