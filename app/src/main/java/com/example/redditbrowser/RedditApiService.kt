package com.example.redditbrowser

import io.reactivex.Single
import retrofit2.http.*

interface RedditApiService {
    @FormUrlEncoded
    @Headers("User-Agent: ${AuthValues.userAgent}")
    @POST("/api/v1/access_token")
    fun getAuth(
        @Header("Authorization") auth: String, @Field("grant_type") grant_type: String, @Field("username") username: String, @Field(
            "password"
        ) password: String
    ): Single<AuthResponse>

    @Headers("User-Agent: ${AuthValues.userAgent}")
    @GET("/api/v1/me")
    fun getMyInfo(@Header("Authorization") token: String): Single<SelfInfo>

    @Headers("User-Agent: ${AuthValues.userAgent}")
    @GET("/subreddits/mine/subscriber")
    fun getMySubscribedSubreddits(@Header("Authorization") token: String): Single<SubredditInfoListWrapper>

    @Headers("User-Agent: ${AuthValues.userAgent}")
    @GET("/")
    fun getMyFrontPage(@Header("Authorization") token: String): Single<PostInfoListWrapper>

    @Headers("User-Agent: ${AuthValues.userAgent}")
    @GET("/")
    fun getMyFrontPage(@Header("Authorization") token: String, @Query("after") after: String?, @Query("count") count: Int): Single<PostInfoListWrapper>
}
