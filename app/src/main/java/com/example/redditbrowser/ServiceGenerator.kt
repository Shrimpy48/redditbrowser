package com.example.redditbrowser

import okhttp3.Cache
import okhttp3.Credentials
import okhttp3.OkHttpClient
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

        if (needsBuilding) {
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

        val oauthInterceptor = AuthenticationInterceptor("Bearer $token")

        if (!redditClientBuilder.interceptors().contains(oauthInterceptor)) {
            redditClientBuilder.addInterceptor(oauthInterceptor)
            needsBuilding = true
        }

        if (needsBuilding) {
            redditClient = redditClientBuilder.build()
            redditBuilder = redditBuilder.client(redditClient)
            reddit = redditBuilder.build()
            redditService = reddit.create(RedditApiService::class.java)
        }

        return redditService
    }

    fun getImgurService(clientId: String): ImgurApiService {
        var needsBuilding = false

        val authInterceptor = AuthenticationInterceptor("Client-ID $clientId")

        if (!imgurClientBuilder.interceptors().contains(authInterceptor)) {
            imgurClientBuilder.addInterceptor(authInterceptor)
            needsBuilding = true
        }

        if (needsBuilding) {
            imgurClient = imgurClientBuilder.build()
            imgurBuilder = imgurBuilder.client(imgurClient)
            imgur = imgurBuilder.build()
            imgurService = imgur.create(ImgurApiService::class.java)
        }

        return imgurService
    }

    fun getGfyAuthService(): GfyAuthApiService = gfyAuthService

    fun getGfyService(token: String): GfyApiService {
        var needsBuilding = false

        val oauthInterceptor = AuthenticationInterceptor("Bearer $token")

        if (!gfyClientBuilder.interceptors().contains(oauthInterceptor)) {
            gfyClientBuilder.addInterceptor(oauthInterceptor)
            needsBuilding = true
        }

        if (needsBuilding) {
            gfyClient = gfyClientBuilder.build()
            gfyBuilder = gfyBuilder.client(gfyClient)
            gfy = gfyBuilder.build()
            gfyService = gfy.create(GfyApiService::class.java)
        }

        return gfyService
    }

}
