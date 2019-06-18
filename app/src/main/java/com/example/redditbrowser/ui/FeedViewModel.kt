package com.example.redditbrowser.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.repositories.PostRepository

class FeedViewModel(repository: PostRepository) : ViewModel() {

    private val feedData = MutableLiveData<Feed>()

    private val repoResult = map(feedData) {
        repository.postsOfFeed(it, 30)
    }

    val posts = switchMap(repoResult) { it.pagedList }!!
    val networkState = switchMap(repoResult) { it.networkState }!!
    val refreshState = switchMap(repoResult) { it.refreshState }!!

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun showFeed(feed: Feed): Boolean {
        if (currentFeed() == feed) {
            return false
        }
        feedData.value = feed
        return true
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    fun currentFeed() = feedData.value

}
