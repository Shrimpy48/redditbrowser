package com.example.redditbrowser.apis.services

import com.example.redditbrowser.apis.responses.ImgurImageListWrapper
import com.example.redditbrowser.apis.responses.ImgurImageWrapper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ImgurApiService {
    @GET("album/{albumHash}/images")
    suspend fun getAlbumImages(@Path("albumHash") albumHash: String): Response<ImgurImageListWrapper>

    @GET("image/{imageHash}")
    suspend fun getImage(@Path("imageHash") imageHash: String): Response<ImgurImageWrapper>
}
