package com.example.redditbrowser.datastructs

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

data class Listing<T>(
    val pagedList: LiveData<PagedList<T>>,
    val refreshState: LiveData<NetworkState>,
    val refresh: () -> Unit
)