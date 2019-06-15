package com.example.redditbrowser

import android.net.Uri
import android.util.Log
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object RedditFetcher {
    private val imgurApiService: ImgurApiService
    private val gfyApiService: GfyApiService
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val gfyTokenResp: Single<GfyAuthResponse>

    init {
        val imgurRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.imgur.com/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val gfyRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.gfycat.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        imgurApiService = imgurRetrofit.create(ImgurApiService::class.java)
        gfyApiService = gfyRetrofit.create(GfyApiService::class.java)

        val request = GfyAuthRequest()
        request.grantType = "client_credentials"
        request.clientId = AuthValues.gfyId
        request.clientSecret = AuthValues.gfySecret

        gfyTokenResp = gfyApiService.getAuth(request).cache()
    }

    fun parsePost(info: PostInfo, listener: ParseListener) {
        val title = info.title
        var contentUri: Uri? = null
        var type: PostType? = null
        var body: String? = null
        var fetched = false
        when {
            info.isSelf != null && info.isSelf!! -> {
                body = info.selftext
                type = PostType.TEXT
                fetched = true
            }
            info.postHint == "image" -> {
                contentUri = Uri.parse(info.url)
                type = PostType.IMAGE
                fetched = true
                Log.d("Fetcher", "fetched image for ${info.url}")
            }
            info.secureMedia != null && info.secureMedia?.redditVideo != null -> {
                contentUri = Uri.parse(info.secureMedia?.redditVideo?.dashUrl)
                type = PostType.VIDEO_DASH
                fetched = true
                Log.d("Fetcher", "fetched dash video for ${info.url}")
            }
            info.media != null && info.media?.redditVideo != null -> {
                contentUri = Uri.parse(info.media?.redditVideo?.dashUrl)
                type = PostType.VIDEO_DASH
                fetched = true
                Log.d("Fetcher", "fetched dash video for ${info.url}")
            }
            info.domain != null && "imgur" in info.domain!! -> {
                val id = info.url?.substringAfterLast("/")?.substringBeforeLast(".")
                imgurApiService.getImage(id!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<ImgurImageWrapper> {
                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onSuccess(resp: ImgurImageWrapper) {
                            when {
                                resp.data?.mp4 != null -> {
                                    contentUri = Uri.parse(resp.data?.mp4)
                                    type = PostType.VIDEO
                                }
                                resp.data?.link != null -> {
                                    contentUri = Uri.parse(resp.data?.link)
                                    type = PostType.IMAGE
                                }
                                else -> {
                                    val placeholder: ProcessedPost =
                                        if (title != null) ProcessedPost(
                                            title,
                                            PostType.URL,
                                            new_content_url = Uri.parse(info.url)
                                        )
                                        else ProcessedPost("ERROR", PostType.TEXT, "Could not fetch post")
                                    Log.d("Imgur", "could not fetch $id (null link)")
                                    listener.onFailure(placeholder)
                                    return
                                }
                            }
                            if (title != null && type != null) {
                                val post = ProcessedPost(title, type!!, body, contentUri)
                                Log.d("Imgur", "fetched $id as $type")
                                listener.onSuccess(post)
                            } else {
                                val placeholder = ProcessedPost("ERROR", PostType.TEXT, "Could not fetch post")
                                Log.d("Imgur", "could not fetch $id (invalid title/type)")
                                listener.onFailure(placeholder)
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.d("Imgur", "fetch $id as image failed due to ${e.localizedMessage}, trying album")
                            imgurApiService.getAlbumImages(id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : SingleObserver<ImgurImageListWrapper> {
                                    override fun onSubscribe(d: Disposable) {
                                        compositeDisposable.add(d)
                                    }

                                    override fun onSuccess(resp: ImgurImageListWrapper) {
                                        // TODO add full album support
                                        if (resp.data?.isEmpty()!!) {
                                            val placeholder: ProcessedPost =
                                                if (title != null) ProcessedPost(
                                                    title,
                                                    PostType.URL,
                                                    new_content_url = Uri.parse(info.url)
                                                )
                                                else ProcessedPost("ERROR", PostType.TEXT, "Could not fetch post")
                                            Log.d("Imgur", "could not fetch $id (no items in album)")
                                            listener.onFailure(placeholder)
                                            return
                                        }
                                        when {
                                            resp.data!![0].mp4 != null -> {
                                                contentUri = Uri.parse(resp.data!![0].mp4)
                                                type = PostType.VIDEO
                                            }
                                            resp.data!![0].link != null -> {
                                                contentUri = Uri.parse(resp.data!![0].link)
                                                type = PostType.IMAGE
                                            }
                                            else -> {
                                                val placeholder: ProcessedPost =
                                                    if (title != null) ProcessedPost(
                                                        title,
                                                        PostType.URL,
                                                        new_content_url = Uri.parse(info.url)
                                                    )
                                                    else ProcessedPost("ERROR", PostType.TEXT, "Could not fetch post")
                                                Log.d("Imgur", "could not fetch $id (item 0 has null link)")
                                                listener.onFailure(placeholder)
                                                return
                                            }
                                        }
                                        if (title != null && type != null) {
                                            val post = ProcessedPost(title, type!!, body, contentUri)
                                            Log.d("Imgur", "fetched first item from $id as $type")
                                            listener.onSuccess(post)
                                        } else {
                                            val placeholder =
                                                ProcessedPost("ERROR", PostType.TEXT, "Could not fetch post")
                                            Log.d("Imgur", "could not fetch $id (invalid title/type)")
                                            listener.onFailure(placeholder)
                                        }
                                    }

                                    override fun onError(e: Throwable) {
                                        Log.e("Imgur", e.localizedMessage)
                                        Log.d("Imgur", "$id could not be fetched")
                                        val placeholder: ProcessedPost =
                                            if (title != null) ProcessedPost(
                                                title,
                                                PostType.URL,
                                                new_content_url = Uri.parse(info.url)
                                            )
                                            else ProcessedPost("ERROR", PostType.TEXT, "Could not fetch post")
                                        listener.onFailure(placeholder)
                                    }
                                })
                        }
                    })
            }
            info.domain != null && "gfycat" in info.domain!! -> {
                val id = info.url?.substringAfterLast("/")?.substringBefore("-")
                gfyTokenResp.flatMap { firstResponse ->
                    gfyApiService.getGfycat(
                        "Bearer ${firstResponse.accessToken}",
                        id!!
                    )
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Gfycat> {
                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onSuccess(resp: Gfycat) {
                            contentUri = Uri.parse(resp.gfyItem?.mp4Url)
                            type = PostType.VIDEO
                            if (title != null && type != null) {
                                val post = ProcessedPost(title, type!!, body, contentUri)
                                listener.onSuccess(post)
                            } else {
                                val placeholder = ProcessedPost("ERROR", PostType.TEXT, "Could not fetch post")
                                Log.d("Gfy", "could not fetch $id (invalid title/type)")
                                listener.onFailure(placeholder)
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.e("Gfycat", e.localizedMessage)
                            Log.d("Gfycat", "$id could not be fetched")
                            val placeholder: ProcessedPost =
                                if (title != null) ProcessedPost(
                                    title,
                                    PostType.URL,
                                    new_content_url = Uri.parse(info.url)
                                )
                                else ProcessedPost("ERROR", PostType.TEXT, "Could not fetch post")
                            listener.onFailure(placeholder)
                        }
                    })
            }
            info.preview != null && info.preview?.redditVideoPreview != null -> {
                contentUri = Uri.parse(info.preview?.redditVideoPreview?.dashUrl)
                type = PostType.VIDEO_DASH
                fetched = true
                Log.d("Fetcher", "using preview for ${info.url}")
            }
            else -> {
                contentUri = Uri.parse(info.url)
                type = PostType.URL
                fetched = true
                Log.d("Fetcher", "could not fetch content for ${info.url}")
            }
        }

        if (fetched && title != null && type != null) {
            val post = ProcessedPost(title, type!!, body, contentUri)
            listener.onSuccess(post)
        } else if (fetched) listener.onFailure(ProcessedPost("ERROR", PostType.TEXT, "Could not fetch post"))
    }

    fun dispose() {
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }

    interface ParseListener {
        fun onSuccess(post: ProcessedPost)

        fun onFailure(placeholder: ProcessedPost)
    }
}


