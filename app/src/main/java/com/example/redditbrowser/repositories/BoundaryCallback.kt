package com.example.redditbrowser.repositories

import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.utils.createStatusLiveData
import java.util.concurrent.Executor

class BoundaryCallback(
    private val feed: Feed,
    private val handleResponse: (Feed, List<Post>) -> Unit,
    private val executor: Executor,
    private val pageSize: Int
) : PagedList.BoundaryCallback<Post>() {

    val helper = PagingRequestHelper(executor)
    val networkState = helper.createStatusLiveData()

    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            // Make API request
            // Provide callback to call insertItemsIntoDb
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Post) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            // Make API request
            // Provide callback to call insertItemsIntoDb
        }
    }

    private fun insertItemsIntoDb(resp: List<Post>, cb: PagingRequestHelper.Request.Callback) {
        executor.execute {
            handleResponse(feed, resp)
            cb.recordSuccess()
        }
    }

    private fun createCallback(cb: PagingRequestHelper.Request.Callback) {

    }
}
