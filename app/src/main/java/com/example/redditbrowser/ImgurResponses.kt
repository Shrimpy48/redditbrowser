package com.example.redditbrowser

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ImgurImage {

    @SerializedName("redditId")
    @Expose
    var id: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("datetime")
    @Expose
    var dateTime: Int? = null
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("animated")
    @Expose
    var animated: Boolean? = null
    @SerializedName("width")
    @Expose
    var width: Int? = null
    @SerializedName("height")
    @Expose
    var height: Int? = null
    @SerializedName("size")
    @Expose
    var size: Int? = null
    @SerializedName("views")
    @Expose
    var views: Int? = null
    @SerializedName("bandwidth")
    @Expose
    var bandwidth: Long? = null
    @SerializedName("deletehash")
    @Expose
    var deleteHash: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("section")
    @Expose
    var section: String? = null
    @SerializedName("link")
    @Expose
    var link: String? = null
    @SerializedName("gifv")
    @Expose
    var gifv: String? = null
    @SerializedName("mp4")
    @Expose
    var mp4: String? = null
    @SerializedName("mp4_size")
    @Expose
    var mp4Size: Int? = null
    @SerializedName("looping")
    @Expose
    var looping: Boolean? = null
    @SerializedName("favorite")
    @Expose
    var favorite: Boolean? = null
    @SerializedName("nsfw")
    @Expose
    var nsfw: Boolean? = null
    @SerializedName("vote")
    @Expose
    var vote: String? = null
    @SerializedName("in_gallery")
    @Expose
    var inGallery: Boolean? = null

}

class ImgurImageWrapper {

    @SerializedName("data")
    @Expose
    var data: ImgurImage? = null
    @SerializedName("success")
    @Expose
    var success: Boolean? = null
    @SerializedName("status")
    @Expose
    var status: Int? = null

}

class ImgurImageListWrapper {

    @SerializedName("data")
    @Expose
    var data: List<ImgurImage>? = null
    @SerializedName("success")
    @Expose
    var success: Boolean? = null
    @SerializedName("status")
    @Expose
    var status: Int? = null

}
