package com.example.redditbrowser

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ImgurApiService {
    @GET("album/{albumHash}/images")
    suspend fun getAlbumImages(@Path("albumHash") albumHash: String): Response<ImgurImageListWrapper>

    @GET("image/{imageHash}")
    suspend fun getImage(@Path("imageHash") imageHash: String): Response<ImgurImageWrapper>
}
