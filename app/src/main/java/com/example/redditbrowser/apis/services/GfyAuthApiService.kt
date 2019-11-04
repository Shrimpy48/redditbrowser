package com.example.redditbrowser.apis.services

import com.example.redditbrowser.apis.responses.GfyAuthRequest
import com.example.redditbrowser.apis.responses.GfyAuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GfyAuthApiService {

    @POST("oauth/token")
    suspend fun getAuth(@Body json: GfyAuthRequest): Response<GfyAuthResponse>

}
