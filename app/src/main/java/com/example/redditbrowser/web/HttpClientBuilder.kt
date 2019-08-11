package com.example.redditbrowser.web

import android.util.Log
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object HttpClientBuilder {

    private val loggingInterceptor = HttpLoggingInterceptor()

    init {
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    }

    private var builder = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .callTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)

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

    fun getNewBuilder(): OkHttpClient.Builder = client.newBuilder()

}
