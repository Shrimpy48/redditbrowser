package com.example.redditbrowser.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.repositories.PostRepository

class FeedViewModel(private val repository: PostRepository) : ViewModel() {

    private val feedData = MutableLiveData<Feed>()

    private val repoResult = map(feedData) {
        repository.postsOfFeed(it, 30)
    }

    val posts = switchMap(repoResult) { it.pagedList }
    val refreshState = switchMap(repoResult) { it.refreshState }

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

    fun currentFeed() = feedData.value


    class Factory(private val repository: PostRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(repository) as T
        }
    }
}
