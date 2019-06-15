package com.example.redditbrowser

import io.reactivex.Single
import retrofit2.http.*

interface GfyApiService {
    @POST("oauth/token")
    fun getAuth(@Body json: GfyAuthRequest): Single<GfyAuthResponse>

    @GET("gfycats/{gfyid}")
    fun getGfycat(@Header("Authorization") token: String, @Path("gfyid") gfyId: String): Single<Gfycat>
}