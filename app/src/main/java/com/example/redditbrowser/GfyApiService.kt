package com.example.redditbrowser

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GfyApiService {

    @GET("gfycats/{gfyid}")
    suspend fun getGfycat(@Path("gfyid") gfyId: String): Response<Gfycat>

}
