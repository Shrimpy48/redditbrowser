package com.example.redditbrowser

import android.util.Log
import okhttp3.Cache
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


object ServiceGenerator {

    private const val REDDIT_BASE_URL = "https://www.reddit.com/"
    private const val REDDIT_OAUTH_BASE_URL = "https://oauth.reddit.com/"
    private const val IMGUR_BASE_URL = "https://api.imgur.com/3/"
    private const val GFY_BASE_URL = "https://api.gfycat.com/v1/"

    private const val cacheSize: Long = 10 * 1024 * 1024

    private var redditAuthClientBuilder = OkHttpClient.Builder()
    private var redditClientBuilder = OkHttpClient.Builder()
    private var imgurClientBuilder = OkHttpClient.Builder()
    private var gfyAuthClientBuilder = OkHttpClient.Builder()
    private var gfyClientBuilder = OkHttpClient.Builder()

    private var redditAuthClient = redditAuthClientBuilder.build()
    private var redditClient = redditClientBuilder.build()
    private var imgurClient = imgurClientBuilder.build()
    private var gfyAuthClient = gfyClientBuilder.build()
    private var gfyClient = gfyClientBuilder.build()

    private var redditAuthBuilder = Retrofit.Builder()
        .baseUrl(REDDIT_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(redditAuthClient)

    private var redditBuilder = Retrofit.Builder()
        .baseUrl(REDDIT_OAUTH_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(redditClient)

    private var imgurBuilder = Retrofit.Builder()
        .baseUrl(IMGUR_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(imgurClient)

    private var gfyAuthBuilder = Retrofit.Builder()
        .baseUrl(GFY_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(gfyAuthClient)

    private var gfyBuilder = Retrofit.Builder()
        .baseUrl(GFY_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(gfyClient)

    private var redditAuth = redditAuthBuilder.build()
    private var reddit = redditBuilder.build()
    private var imgur = imgurBuilder.build()
    private var gfyAuth = gfyAuthBuilder.build()
    private var gfy = gfyBuilder.build()

    private var redditAuthService = redditAuth.create(RedditAuthApiService::class.java)
    private var redditService = reddit.create(RedditApiService::class.java)
    private var imgurService = imgur.create(ImgurApiService::class.java)
    private var gfyAuthService = gfyAuth.create(GfyAuthApiService::class.java)
    private var gfyService = gfy.create(GfyApiService::class.java)

    private val redditAgentInterceptor = AgentInterceptor(AuthValues.userAgent)

    private val redditAuthInterceptor = AuthenticationInterceptor(
        Credentials.basic(AuthValues.redditId, AuthValues.redditSecret)
    )

    private val imgurAuthInterceptor = AuthenticationInterceptor("Client-ID ${AuthValues.imgurId}")

    private val loggingInterceptor = HttpLoggingInterceptor()

    init {
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    }

    private var redditToken: String = ""
    private lateinit var redditTokenAuthInterceptor: AuthenticationInterceptor

    private var gfyToken: String = ""
    private lateinit var gfyTokenAuthInterceptor: AuthenticationInterceptor


    fun setCache(cacheDir: File) {
        val cache = Cache(cacheDir, cacheSize)
        redditAuthClientBuilder = redditAuthClientBuilder.cache(cache)
        redditClientBuilder = redditClientBuilder.cache(cache)
        imgurClientBuilder = imgurClientBuilder.cache(cache)
        gfyAuthClientBuilder = gfyAuthClientBuilder.cache(cache)
        gfyClientBuilder = gfyClientBuilder.cache(cache)
    }

    fun getredditAuthService(): RedditAuthApiService {
        var needsBuilding = false
        if (!redditAuthClientBuilder.interceptors().contains(redditAgentInterceptor)) {
            redditAuthClientBuilder.addInterceptor(redditAgentInterceptor)
            needsBuilding = true
        }
        if (!redditAuthClientBuilder.interceptors().contains(redditAuthInterceptor)) {
            redditAuthClientBuilder.addInterceptor(redditAuthInterceptor)
            needsBuilding = true
        }

        if (!redditAuthClientBuilder.interceptors().contains(loggingInterceptor)) {
            redditAuthClientBuilder.addInterceptor(loggingInterceptor)
            needsBuilding = true
        }

        if (needsBuilding) {
            Log.d("RedditAuthService", "Rebuilt")
            redditAuthClient = redditAuthClientBuilder.build()
            redditAuthBuilder = redditAuthBuilder.client(redditAuthClient)
            redditAuth = redditAuthBuilder.build()
            redditAuthService = redditAuth.create(RedditAuthApiService::class.java)
        }

        return redditAuthService
    }

    fun getredditService(token: String): RedditApiService {
        var needsBuilding = false
        if (!redditClientBuilder.interceptors().contains(redditAgentInterceptor)) {
            redditClientBuilder.addInterceptor(redditAgentInterceptor)
            needsBuilding = true
        }

        if (redditToken != token) {
            redditTokenAuthInterceptor = AuthenticationInterceptor("Bearer $token")
            redditToken = token
        }

        if (!redditClientBuilder.interceptors().contains(redditTokenAuthInterceptor)) {
            redditClientBuilder.addInterceptor(redditTokenAuthInterceptor)
            needsBuilding = true
        }

        if (!redditClientBuilder.interceptors().contains(loggingInterceptor)) {
            redditClientBuilder.addInterceptor(loggingInterceptor)
            needsBuilding = true
        }

        if (needsBuilding) {
            Log.d("RedditService", "Rebuilt")
            redditClient = redditClientBuilder.build()
            redditBuilder = redditBuilder.client(redditClient)
            reddit = redditBuilder.build()
            redditService = reddit.create(RedditApiService::class.java)
        }

        return redditService
    }

    fun getImgurService(): ImgurApiService {
        var needsBuilding = false

        if (!imgurClientBuilder.interceptors().contains(imgurAuthInterceptor)) {
            imgurClientBuilder.addInterceptor(imgurAuthInterceptor)
            needsBuilding = true
        }

        if (!imgurClientBuilder.interceptors().contains(loggingInterceptor)) {
            imgurClientBuilder.addInterceptor(loggingInterceptor)
            needsBuilding = true
        }

        if (needsBuilding) {
            Log.d("ImgurService", "Rebuilt")
            imgurClient = imgurClientBuilder.build()
            imgurBuilder = imgurBuilder.client(imgurClient)
            imgur = imgurBuilder.build()
            imgurService = imgur.create(ImgurApiService::class.java)
        }

        return imgurService
    }

    fun getGfyAuthService(): GfyAuthApiService {
        var needsBuilding = false

        if (!gfyAuthClientBuilder.interceptors().contains(loggingInterceptor)) {
            gfyAuthClientBuilder.addInterceptor(loggingInterceptor)
            needsBuilding = true
        }

        if (needsBuilding) {
            Log.d("GfyAuthService", "Rebuilt")
            gfyAuthClient = gfyAuthClientBuilder.build()
            gfyAuthBuilder = gfyAuthBuilder.client(gfyAuthClient)
            gfyAuth = gfyAuthBuilder.build()
            gfyAuthService = gfyAuth.create(GfyAuthApiService::class.java)
        }

        return gfyAuthService
    }

    fun getGfyService(token: String): GfyApiService {
        var needsBuilding = false

        if (gfyToken != token) {
            gfyTokenAuthInterceptor = AuthenticationInterceptor("Bearer $token")
            gfyToken = token
        }

        if (!gfyClientBuilder.interceptors().contains(gfyTokenAuthInterceptor)) {
            gfyClientBuilder.addInterceptor(gfyTokenAuthInterceptor)
            needsBuilding = true
        }

        if (!gfyClientBuilder.interceptors().contains(loggingInterceptor)) {
            gfyClientBuilder.addInterceptor(loggingInterceptor)
            needsBuilding = true
        }

        if (needsBuilding) {
            Log.d("GfyService", "Rebuilt")
            gfyClient = gfyClientBuilder.build()
            gfyBuilder = gfyBuilder.client(gfyClient)
            gfy = gfyBuilder.build()
            gfyService = gfy.create(GfyApiService::class.java)
        }

        return gfyService
    }

}
