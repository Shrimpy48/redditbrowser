package com.example.redditbrowser

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ImgurApiService {
    @Headers("Authorization: Client-ID ${AuthValues.imgurId}")
    @GET("album/{albumHash}/images")
    fun getAlbumImages(@Path("albumHash") albumHash: String): Single<ImgurImageListWrapper>

    @Headers("Authorization: Client-ID ${AuthValues.imgurId}")
    @GET("image/{imageHash}")
    fun getImage(@Path("imageHash") imageHash: String): Single<ImgurImageWrapper>
}
