package com.example.redditbrowser.apis.services

import com.example.redditbrowser.apis.responses.MultiInfoWrapperBasic
import com.example.redditbrowser.apis.responses.PostInfoListWrapper
import com.example.redditbrowser.apis.responses.SelfInfo
import com.example.redditbrowser.apis.responses.SubredditInfoListWrapper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditApiService {

    @GET("api/v1/me?raw_json=1")
    suspend fun getMyInfo(): Response<SelfInfo>

    @GET("subreddits/mine/subscriber?raw_json=1")
    suspend fun getMySubscribedSubreddits(@Query("limit") limit: Int): Response<SubredditInfoListWrapper>

    @GET("subreddits/mine/subscriber?raw_json=1")
    suspend fun getMySubscribedSubreddits(
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<SubredditInfoListWrapper>

    @GET("api/multi/mine?raw_json=1")
    suspend fun getMyMultis(): Response<List<MultiInfoWrapperBasic>>

    @GET("/?raw_json=1")
    suspend fun getMyFrontPagePosts(@Query("limit") limit: Int): Response<PostInfoListWrapper>

    @GET("/?raw_json=1")
    suspend fun getMyFrontPagePosts(
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<PostInfoListWrapper>

    @GET("/{sort}?raw_json=1")
    suspend fun getMyFrontPagePosts(@Path("sort") sort: String, @Query("limit") limit: Int): Response<PostInfoListWrapper>

    @GET("/{sort}?raw_json=1")
    suspend fun getMyFrontPagePosts(
        @Path("sort") sort: String,
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<PostInfoListWrapper>

    @GET("/{sort}?raw_json=1")
    suspend fun getMyFrontPagePosts(@Path("sort") sort: String, @Query("t") period: String, @Query("limit") limit: Int): Response<PostInfoListWrapper>

    @GET("/{sort}?raw_json=1")
    suspend fun getMyFrontPagePosts(
        @Path("sort") sort: String, @Query("t") period: String,
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<PostInfoListWrapper>

    @GET("r/{subreddit}?raw_json=1")
    suspend fun getSubredditPosts(@Path("subreddit") subreddit: String, @Query("limit") limit: Int): Response<PostInfoListWrapper>

    @GET("r/{subreddit}?raw_json=1")
    suspend fun getSubredditPosts(
        @Path("subreddit") subreddit: String,
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<PostInfoListWrapper>

    @GET("r/{subreddit}/{sort}?raw_json=1")
    suspend fun getSubredditPosts(@Path("subreddit") subreddit: String, @Path("sort") sort: String, @Query("limit") limit: Int): Response<PostInfoListWrapper>

    @GET("r/{subreddit}/{sort}?raw_json=1")
    suspend fun getSubredditPosts(
        @Path("subreddit") subreddit: String, @Path("sort") sort: String,
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<PostInfoListWrapper>

    @GET("r/{subreddit}/{sort}?raw_json=1")
    suspend fun getSubredditPosts(
        @Path("subreddit") subreddit: String, @Path("sort") sort: String, @Query("t") period: String, @Query(
            "limit"
        ) limit: Int
    ): Response<PostInfoListWrapper>

    @GET("r/{subreddit}/{sort}?raw_json=1")
    suspend fun getSubredditPosts(
        @Path("subreddit") subreddit: String, @Path("sort") sort: String, @Query("t") period: String,
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<PostInfoListWrapper>

    @GET("me/m/{multi}?raw_json=1")
    suspend fun getMyMultiPosts(@Path("multi") multi: String, @Query("limit") limit: Int): Response<PostInfoListWrapper>

    @GET("me/m/{multi}?raw_json=1")
    suspend fun getMyMultiPosts(
        @Path("multi") multi: String,
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<PostInfoListWrapper>

    @GET("me/m/{multi}/{sort}?raw_json=1")
    suspend fun getMyMultiPosts(@Path("multi") multi: String, @Path("sort") sort: String, @Query("limit") limit: Int): Response<PostInfoListWrapper>

    @GET("me/m/{multi}/{sort}?raw_json=1")
    suspend fun getMyMultiPosts(
        @Path("multi") multi: String, @Path("sort") sort: String,
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<PostInfoListWrapper>

    @GET("me/m/{multi}/{sort}?raw_json=1")
    suspend fun getMyMultiPosts(
        @Path("multi") multi: String, @Path("sort") sort: String, @Query("t") period: String, @Query(
            "limit"
        ) limit: Int
    ): Response<PostInfoListWrapper>

    @GET("me/m/{multi}/{sort}?raw_json=1")
    suspend fun getMyMultiPosts(
        @Path("multi") multi: String, @Path("sort") sort: String, @Query("t") period: String,
        @Query("after") after: String?,
        @Query("count") count: Int,
        @Query("limit") limit: Int
    ): Response<PostInfoListWrapper>

}
