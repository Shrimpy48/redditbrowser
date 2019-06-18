package com.example.redditbrowser.apis.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GfyAuthResponse {

    @SerializedName("token_type")
    @Expose
    var tokenType: String? = null
    @SerializedName("scope")
    @Expose
    var scope: String? = null
    @SerializedName("expires_in")
    @Expose
    var expiresIn: Int? = null
    @SerializedName("access_token")
    @Expose
    var accessToken: String? = null

}

class GfyAuthRequest {

    @SerializedName("grant_type")
    @Expose
    var grantType: String? = null
    @SerializedName("client_id")
    @Expose
    var clientId: String? = null
    @SerializedName("client_secret")
    @Expose
    var clientSecret: String? = null

}

class Gfycat {

    @SerializedName("gfyItem")
    @Expose
    var gfyItem: GfyItem? = null

}

class GfyItem {

    @SerializedName("gfyId")
    @Expose
    var gfyId: String? = null
    @SerializedName("gfyName")
    @Expose
    var gfyName: String? = null
    @SerializedName("gfyNumber")
    @Expose
    var gfyNumber: String? = null
    @SerializedName("webmUrl")
    @Expose
    var webmUrl: String? = null
    @SerializedName("gifUrl")
    @Expose
    var gifUrl: String? = null
    @SerializedName("mobileUrl")
    @Expose
    var mobileUrl: String? = null
    @SerializedName("mobilePosterUrl")
    @Expose
    var mobilePosterUrl: String? = null
    @SerializedName("miniUrl")
    @Expose
    var miniUrl: String? = null
    @SerializedName("miniPosterUrl")
    @Expose
    var miniPosterUrl: String? = null
    @SerializedName("posterUrl")
    @Expose
    var posterUrl: String? = null
    @SerializedName("thumb100PosterUrl")
    @Expose
    var thumb100PosterUrl: String? = null
    @SerializedName("max5mbGif")
    @Expose
    var max5mbGif: String? = null
    @SerializedName("max2mbGif")
    @Expose
    var max2mbGif: String? = null
    @SerializedName("max1mbGif")
    @Expose
    var max1mbGif: String? = null
    @SerializedName("gif100px")
    @Expose
    var gif100px: String? = null
    @SerializedName("width")
    @Expose
    var width: Int? = null
    @SerializedName("height")
    @Expose
    var height: Int? = null
    @SerializedName("avgColor")
    @Expose
    var avgColor: String? = null
    @SerializedName("frameRate")
    @Expose
    var frameRate: Float? = null
    @SerializedName("numFrames")
    @Expose
    var numFrames: Int? = null
    @SerializedName("mp4Size")
    @Expose
    var mp4Size: Int? = null
    @SerializedName("webmSize")
    @Expose
    var webmSize: Int? = null
    @SerializedName("gifSize")
    @Expose
    var gifSize: Int? = null
    @SerializedName("source")
    @Expose
    var source: Int? = null
    @SerializedName("createDate")
    @Expose
    var createDate: Int? = null
    @SerializedName("nsfw")
    @Expose
    var nsfw: String? = null
    @SerializedName("mp4Url")
    @Expose
    var mp4Url: String? = null
    @SerializedName("likes")
    @Expose
    var likes: String? = null
    @SerializedName("published")
    @Expose
    var published: Int? = null
    @SerializedName("dislikes")
    @Expose
    var dislikes: String? = null
    @SerializedName("extraLemmas")
    @Expose
    var extraLemmas: String? = null
    @SerializedName("md5")
    @Expose
    var md5: String? = null
    @SerializedName("views")
    @Expose
    var views: Int? = null
    @SerializedName("tags")
    @Expose
    var tags: List<String>? = null
    @SerializedName("userName")
    @Expose
    var userName: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("languageText")
    @Expose
    var languageText: String? = null
    @SerializedName("languageCategories")
    @Expose
    var languageCategories: Any? = null
    @SerializedName("subreddit")
    @Expose
    var subreddit: String? = null
    @SerializedName("redditId")
    @Expose
    var redditId: String? = null
    @SerializedName("redditIdText")
    @Expose
    var redditIdText: String? = null
    @SerializedName("domainWhitelist")
    @Expose
    var domainWhitelist: List<Any>? = null

}
