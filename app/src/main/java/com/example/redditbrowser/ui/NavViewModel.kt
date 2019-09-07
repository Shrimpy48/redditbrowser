package com.example.redditbrowser.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.redditbrowser.apis.ApiFetcher
import com.example.redditbrowser.apis.SubscribedDataSource
import com.example.redditbrowser.datastructs.Multi

class NavViewModel : ViewModel() {
    val username: LiveData<String> = liveData {
        val data = ApiFetcher.getMyInfo().name!!
        emit(data)
    }
    val multis: LiveData<List<Multi>> = liveData {
        val data = ApiFetcher.getMyMultis()
        emit(data)
    }
    val subreddits: LiveData<PagedList<String>> = SubscribedDataSource.Factory().toLiveData(pageSize = 25)
}
