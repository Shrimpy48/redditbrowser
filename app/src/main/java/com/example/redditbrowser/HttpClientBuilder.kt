package com.example.redditbrowser

import android.util.Log
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object HttpClientBuilder {

    private val loggingInterceptor = HttpLoggingInterceptor()

    init {
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    }

    private var builder = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)

    private var client = builder.build()

    private var cached = false

    fun setCache(cache: Cache) {
        builder = builder.cache(cache)
        client = builder.build()
        cached = true
    }

    fun getClient(): OkHttpClient {
        if (!cached) Log.w("HttpClient", "Cache not enabled")
        return client
    }

    fun getNewBuilder() = client.newBuilder()

}
