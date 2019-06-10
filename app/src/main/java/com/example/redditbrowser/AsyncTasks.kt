package com.example.redditbrowser

import android.os.AsyncTask

class FetchFeedTask(delegate: FeedTaskResponse, private val fetcher: RedditFetcher) :
    AsyncTask<FeedInfo, Void, ArrayList<ArrayList<PostInfo>>>(),
    FeedTaskResponse by delegate {
    override fun doInBackground(vararg params: FeedInfo?): ArrayList<ArrayList<PostInfo>> {
        val res = ArrayList<ArrayList<PostInfo>>(params.size)
        for (info in params) {
            if (isCancelled) {
                break
            }
            if (info != null) {
                val feedposts = fetcher.fetchPosts(info)
                res.add(feedposts)
            }
        }
        return res
    }

    override fun onPostExecute(result: ArrayList<ArrayList<PostInfo>>?) {
        super.onPostExecute(result)
        if (result != null) processFinish(result)
    }
}

interface FeedTaskResponse {
    fun processFinish(output: ArrayList<ArrayList<PostInfo>>)
}
