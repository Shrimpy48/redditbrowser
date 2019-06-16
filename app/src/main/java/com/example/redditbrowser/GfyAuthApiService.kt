package com.example.redditbrowser

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GfyAuthApiService {

    @POST("oauth/token")
    suspend fun getAuth(@Body json: GfyAuthRequest): Response<GfyAuthResponse>

}
