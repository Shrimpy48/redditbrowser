package com.example.redditbrowser.apis.services

import com.example.redditbrowser.apis.responses.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditApiService {

    @GET("api/v1/me")
    suspend fun getMyInfo(): Response<SelfInfo>

    @GET("subreddits/mine/subscriber")
    suspend fun getMySubscribedSubreddits(): Response<SubredditInfoListWrapper>

    @GET("subreddits/mine/subscriber")
    suspend fun getMySubscribedSubreddits(
        @Query("after") after: String?,
        @Query("count") count: Int
    ): Response<SubredditInfoListWrapper>

    @GET("subreddits/mine/subscriber")
    suspend fun getMySubscribedSubreddits(@Query("limit") limit: Int): Response<SubredditInfoListWrapper>

    @GET("subreddits/mine/subscriber")
    suspend fun getMySubscribedSubreddits(
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<SubredditInfoListWrapper>

    @GET("api/multi/mine")
    suspend fun getMyMultis(): Response<List<MultiInfoWrapperBasic>>

    @GET("api/multi/mine?expand_srs=true")
    suspend fun getMyMultisFull(): Response<List<MultiInfoWrapper>>

    @GET("/")
    suspend fun getMyFrontPagePosts(): Response<PostInfoListWrapper>

    @GET("/")
    suspend fun getMyFrontPagePosts(
        @Query("after") after: String?,
        @Query("count") count: Int
    ): Response<PostInfoListWrapper>

    @GET("r/{subreddit}")
    suspend fun getSubredditPosts(@Path("subreddit") subreddit: String): Response<PostInfoListWrapper>

    @GET("r/{subreddit}")
    suspend fun getSubredditPosts(
        @Path("subreddit") subreddit: String,
        @Query("after") after: String?,
        @Query("count") count: Int
    ): Response<PostInfoListWrapper>

    @GET("me/m/{multi}")
    suspend fun getMyMultiPosts(@Path("multi") multi: String): Response<PostInfoListWrapper>

    @GET("me/m/{multi}")
    suspend fun getMyMultiPosts(
        @Path("multi") multi: String,
        @Query("after") after: String?,
        @Query("count") count: Int
    ): Response<PostInfoListWrapper>

}
