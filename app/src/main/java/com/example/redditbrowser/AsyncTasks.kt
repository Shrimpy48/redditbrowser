package com.example.redditbrowser

import android.os.AsyncTask

class FetchFeedTask(delegate: FeedTaskResponse) : AsyncTask<FeedInfo, Void, ArrayList<ArrayList<PostInfo>>>(),
    FeedTaskResponse by delegate {
    override fun doInBackground(vararg params: FeedInfo?): ArrayList<ArrayList<PostInfo>> {
        val res = ArrayList<ArrayList<PostInfo>>(params.size)
        val postFetcher = PostFetcher()
        for (info in params) {
            if (isCancelled) {
                break
            }
            if (info != null) {
                val feedposts = postFetcher.fetchPosts(info)
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

class FetchPostTask(delegate: PostTaskResponse) : AsyncTask<PostInfo, Void, ArrayList<PostData>>(),
    PostTaskResponse by delegate {
    override fun doInBackground(vararg params: PostInfo?): ArrayList<PostData> {
        val res = ArrayList<PostData>(params.size)
        val postFetcher = PostFetcher()
        for (info in params) {
            if (isCancelled) {
                break
            }
            if (info != null) {
                val postsInfo = postFetcher.fetchData(info)
                res.add(postsInfo)
            }
        }
        return res
    }

    override fun onPostExecute(result: ArrayList<PostData>?) {
        super.onPostExecute(result)
        if (result != null) processFinish(result)
    }
}

interface PostTaskResponse {
    fun processFinish(output: ArrayList<PostData>)
}
