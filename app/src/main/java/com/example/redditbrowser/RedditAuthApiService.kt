package com.example.redditbrowser

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RedditAuthApiService {

    @FormUrlEncoded
    @POST("api/v1/access_token")
    suspend fun getAuth(
        @Field("grant_type") grant_type: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<AuthResponse>

}