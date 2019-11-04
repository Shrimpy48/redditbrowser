package com.example.redditbrowser.repositories

import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import com.example.redditbrowser.apis.ApiFetcher
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.datastructs.Post
import java.util.concurrent.Executor

class BoundaryCallback(
    private val feed: Feed,
    private val pageSize: Int,
    private val handleResponse: (Feed, List<Post>, Int) -> Unit,
    private val executor: Executor
) : PagedList.BoundaryCallback<Post>() {

    private val helper = PagingRequestHelper(executor)

    private var count = 0
    private var after: String? = null

    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            ApiFetcher.getFeedPosts(feed, pageSize, object : ApiFetcher.Listener<ApiFetcher.Page<Post>> {
                override fun onComplete(result: ApiFetcher.Page<Post>) {
                    count = result.count
                    after = result.after
                    insertItemsIntoDb(result.items, it)
                }

                override fun onFailure(t: Throwable) {
                    it.recordFailure(t)
                }
            })
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Post) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            ApiFetcher.getFeedPosts(feed, after, count, pageSize, object : ApiFetcher.Listener<ApiFetcher.Page<Post>> {
                override fun onComplete(result: ApiFetcher.Page<Post>) {
                    count = result.count
                    after = result.after
                    insertItemsIntoDb(result.items, it)
                }

                override fun onFailure(t: Throwable) {
                    it.recordFailure(t)
                }
            })
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: Post) {}

    private fun insertItemsIntoDb(resp: List<Post>, it: PagingRequestHelper.Request.Callback) {
        executor.execute {
            handleResponse(feed, resp, count - resp.size)
            it.recordSuccess()
        }
    }
}
