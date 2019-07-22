package com.example.redditbrowser.apis

import android.util.Log
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource

class SubscribedDataSource : PageKeyedDataSource<String, String>() {

    private var count: Int = 0

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, String>) {
        ApiFetcher.getMySubscribedSubreddits(
            params.requestedLoadSize,
            object : ApiFetcher.Listener<ApiFetcher.Page<String>> {
                override fun onComplete(result: ApiFetcher.Page<String>) {
                    count = result.count
                    callback.onResult(result.items, result.before, result.after)
                }

                override fun onFailure(t: Throwable) {
                    Log.e("DataSource", "Could not fetch initial page")
                }
            })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, String>) {}

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, String>) {
        ApiFetcher.getMySubscribedSubreddits(
            params.key,
            count,
            params.requestedLoadSize,
            object : ApiFetcher.Listener<ApiFetcher.Page<String>> {
                override fun onComplete(result: ApiFetcher.Page<String>) {
                    count = result.count
                    callback.onResult(result.items, result.after)
                }

                override fun onFailure(t: Throwable) {
                    Log.e("DataSource", "Could not fetch page after ${params.key}")
                }
            })
    }

    class Factory : DataSource.Factory<String, String>() {
        override fun create(): DataSource<String, String> {
            return SubscribedDataSource()
        }
    }
}