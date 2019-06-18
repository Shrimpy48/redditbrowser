package com.example.redditbrowser.apis

import android.util.Log
import com.example.redditbrowser.apis.services.*
import com.example.redditbrowser.web.AgentInterceptor
import com.example.redditbrowser.web.AuthenticationInterceptor
import com.example.redditbrowser.web.HttpClientBuilder
import okhttp3.Credentials
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceGenerator {

    private const val REDDIT_BASE_URL = "https://www.reddit.com/"
    private const val REDDIT_OAUTH_BASE_URL = "https://oauth.reddit.com/"
    private const val IMGUR_BASE_URL = "https://api.imgur.com/3/"
    private const val GFY_BASE_URL = "https://api.gfycat.com/v1/"

    private var redditAuthClient = HttpClientBuilder.getClient()
    private var redditClient = HttpClientBuilder.getClient()
    private var imgurClient = HttpClientBuilder.getClient()
    private var gfyAuthClient = HttpClientBuilder.getClient()
    private var gfyClient = HttpClientBuilder.getClient()

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

    private val imgurAuthInterceptor =
        AuthenticationInterceptor("Client-ID ${AuthValues.imgurId}")

    private var redditToken: String = ""
    private lateinit var redditTokenAuthInterceptor: AuthenticationInterceptor

    private var gfyToken: String = ""
    private lateinit var gfyTokenAuthInterceptor: AuthenticationInterceptor


    fun getRedditAuthService(): RedditAuthApiService {
        var needsBuilding = false
        if (!redditAuthClient.interceptors().contains(redditAgentInterceptor)) {
            needsBuilding = true
        }
        if (!redditAuthClient.interceptors().contains(redditAuthInterceptor)) {
            needsBuilding = true
        }

        if (needsBuilding) {
            Log.d("RedditAuthService", "Rebuilt")
            redditAuthClient = HttpClientBuilder.getNewBuilder()
                .addInterceptor(redditAgentInterceptor)
                .addInterceptor(redditAuthInterceptor)
                .build()
            redditAuthBuilder = redditAuthBuilder.client(
                redditAuthClient
            )
            redditAuth = redditAuthBuilder.build()
            redditAuthService = redditAuth.create(
                RedditAuthApiService::class.java
            )
        }

        return redditAuthService
    }

    fun getRedditService(token: String): RedditApiService {
        var needsBuilding = false
        if (!redditClient.interceptors().contains(redditAgentInterceptor)) {
            needsBuilding = true
        }

        if (redditToken != token) {
            redditTokenAuthInterceptor =
                AuthenticationInterceptor("Bearer $token")
            redditToken = token
        }

        if (!redditClient.interceptors().contains(redditTokenAuthInterceptor)) {
            needsBuilding = true
        }

        if (needsBuilding) {
            Log.d("RedditService", "Rebuilt")
            redditClient = HttpClientBuilder.getNewBuilder()
                .addInterceptor(redditAgentInterceptor)
                .addInterceptor(redditTokenAuthInterceptor)
                .build()
            redditBuilder = redditBuilder.client(
                redditClient
            )
            reddit = redditBuilder.build()
            redditService = reddit.create(
                RedditApiService::class.java
            )
        }

        return redditService
    }

    fun getImgurService(): ImgurApiService {
        var needsBuilding = false

        if (!imgurClient.interceptors().contains(imgurAuthInterceptor)) {
            needsBuilding = true
        }

        if (needsBuilding) {
            Log.d("ImgurService", "Rebuilt")
            imgurClient = HttpClientBuilder.getNewBuilder()
                .addInterceptor(imgurAuthInterceptor)
                .build()
            imgurBuilder = imgurBuilder.client(
                imgurClient
            )
            imgur = imgurBuilder.build()
            imgurService = imgur.create(
                ImgurApiService::class.java
            )
        }

        return imgurService
    }

    fun getGfyAuthService(): GfyAuthApiService {

        return gfyAuthService
    }

    fun getGfyService(token: String): GfyApiService {
        var needsBuilding = false

        if (gfyToken != token) {
            gfyTokenAuthInterceptor =
                AuthenticationInterceptor("Bearer $token")
            gfyToken = token
        }

        if (!gfyClient.interceptors().contains(gfyTokenAuthInterceptor)) {
            needsBuilding = true
        }

        if (needsBuilding) {
            Log.d("GfyService", "Rebuilt")
            gfyClient = HttpClientBuilder.getNewBuilder()
                .addInterceptor(gfyTokenAuthInterceptor)
                .build()
            gfyBuilder = gfyBuilder.client(
                gfyClient
            )
            gfy = gfyBuilder.build()
            gfyService = gfy.create(
                GfyApiService::class.java
            )
        }

        return gfyService
    }

}
