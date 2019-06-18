package com.example.redditbrowser.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingRequestHelper
import com.example.redditbrowser.datastructs.NetworkState

private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
    return PagingRequestHelper.RequestType.values().mapNotNull {
        report.getErrorFor(it)?.message
    }.first()
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
    val liveData = MutableLiveData<NetworkState>()
    addListener { report ->
        when {
            report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
            else -> liveData.postValue(NetworkState.LOADED)
        }
    }
    return liveData
}