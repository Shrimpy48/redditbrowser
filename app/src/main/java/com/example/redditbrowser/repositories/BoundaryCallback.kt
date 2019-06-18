package com.example.redditbrowser.repositories

import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import com.example.redditbrowser.apis.ApiFetcher
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.utils.createStatusLiveData
import java.util.concurrent.Executor

class BoundaryCallback(
    private val feed: Feed,
    private val handleResponse: (Feed, List<Post>) -> Unit,
    private val executor: Executor
) : PagedList.BoundaryCallback<Post>() {

    val helper = PagingRequestHelper(executor)
    val networkState = helper.createStatusLiveData()

    var count = 0

    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            ApiFetcher.getFeedPosts(feed, object : ApiFetcher.Listener<List<Post>> {
                override fun onComplete(result: List<Post>) {
                    insertItemsIntoDb(result, it)
                    count += result.size
                }
            })
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Post) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            ApiFetcher.getFeedPosts(feed, itemAtEnd.name, count, object : ApiFetcher.Listener<List<Post>> {
                override fun onComplete(result: List<Post>) {
                    insertItemsIntoDb(result, it)
                    count += result.size
                }
            })
        }
    }

    private fun insertItemsIntoDb(resp: List<Post>, cb: PagingRequestHelper.Request.Callback) {
        executor.execute {
            handleResponse(feed, resp)
            cb.recordSuccess()
        }
    }
}
