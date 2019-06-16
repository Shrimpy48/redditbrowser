package com.example.redditbrowser

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


object RedditFetcher {

    private val redditAuth = ServiceGenerator.getredditAuthService()
    private val gfyAuth = ServiceGenerator.getGfyAuthService()

    private var redditToken: String? = null
    private var redditExpireTime: Long? = null

    private var gfyToken: String? = null
    private var gfyExpireTime: Long? = null

    private val redditMutex = Mutex()
    private val gfyMutex = Mutex()

    private val redditServiceMutex = Mutex()
    private val imgurServiceMutex = Mutex()
    private val gfyServiceMutex = Mutex()


    private suspend fun getRedditToken(): String? {
        redditMutex.withLock {
            if (redditToken != null && System.currentTimeMillis() < redditExpireTime!!) {
                return redditToken
            }
            val authResp = redditAuth.getAuth("password", AuthValues.redditUsername, AuthValues.redditPassword)
            return if (authResp.isSuccessful) {
                redditToken = authResp.body()?.accessToken!!
                redditExpireTime = System.currentTimeMillis() + (authResp.body()?.expiresIn!! * 1000)
                redditToken
            } else {
                Log.e("Reddit auth", authResp.message())
                null
            }
        }
    }

    private suspend fun getGfyToken(): String? {
        gfyMutex.withLock {
            if (gfyToken != null && System.currentTimeMillis() < gfyExpireTime!!) {
                return gfyToken
            }
            val request = GfyAuthRequest()
            request.grantType = "client_credentials"
            request.clientId = AuthValues.gfyId
            request.clientSecret = AuthValues.gfySecret
            val authResp = gfyAuth.getAuth(request)
            return if (authResp.isSuccessful) {
                gfyToken = authResp.body()?.accessToken
                gfyExpireTime = System.currentTimeMillis() + (authResp.body()?.expiresIn!! * 1000)
                gfyToken
            } else {
                Log.e("Gfy auth", authResp.message())
                null
            }
        }
    }

    private suspend fun parseImgurImage(title: String, url: String): ProcessedPost? {
        val id = url.substringAfterLast("/").substringBeforeLast(".")
        var service: ImgurApiService? = null
        imgurServiceMutex.withLock {
            service = ServiceGenerator.getImgurService()
        }
        val res = service!!.getImage(id)
        if (res.isSuccessful) {
            val contentUri: Uri
            val type: PostType
            when {
                res.body()?.data?.mp4 != null -> {
                    contentUri = Uri.parse(res.body()?.data?.mp4)
                    type = PostType.VIDEO
                }
                res.body()?.data?.link != null -> {
                    contentUri = Uri.parse(res.body()?.data?.link)
                    type = PostType.IMAGE
                }
                else -> {
                    contentUri = Uri.parse(url)
                    type = PostType.URL
                }
            }
            return ProcessedPost(title, type, new_content_url = contentUri)
        }
        Log.d("Imgur image", "ID $id not successfully fetched")
        val contentUri = Uri.parse(url)
        val type = PostType.URL
        return ProcessedPost(title, type, new_content_url = contentUri)
    }

    private suspend fun parseImgurAlbum(title: String, url: String): ProcessedPost? {
        val id = url.substringAfterLast("/").substringBeforeLast(".")
        var service: ImgurApiService? = null
        imgurServiceMutex.withLock {
            service = ServiceGenerator.getImgurService()
        }
        val res = service!!.getAlbumImages(id)
        if (res.isSuccessful) {
            // TODO fully handle albums
            val contentUri: Uri
            val type: PostType
            when {
                res.body()?.data.isNullOrEmpty() -> return null

                res.body()?.data!![0].mp4 != null -> {
                    contentUri = Uri.parse(res.body()?.data!![0].mp4)
                    type = PostType.VIDEO
                }
                res.body()?.data!![0].link != null -> {
                    contentUri = Uri.parse(res.body()?.data!![0].link)
                    type = PostType.IMAGE
                }
                else -> {
                    contentUri = Uri.parse(url)
                    type = PostType.URL
                }
            }
            return ProcessedPost(title, type, new_content_url = contentUri)
        }
        Log.d("Imgur album", "ID $id not successfully fetched")
        val contentUri = Uri.parse(url)
        val type = PostType.URL
        return ProcessedPost(title, type, new_content_url = contentUri)
    }

    private suspend fun parseGfy(title: String, url: String): ProcessedPost? {
        val id = url.substringAfterLast("/").substringBeforeLast(".").substringBefore("-")
        val token = getGfyToken() ?: return null
        val contentUri: Uri
        val type: PostType
        var service: GfyApiService? = null
        gfyServiceMutex.withLock {
            service = ServiceGenerator.getGfyService(token)
        }
        val res = service!!.getGfycat(id)
        if (res.isSuccessful) {
            contentUri = Uri.parse(res.body()?.gfyItem?.mp4Url)
            type = PostType.VIDEO
        } else {
            contentUri = Uri.parse(url)
            type = PostType.URL
            Log.d("Gfy", "ID $id not successfully fetched")
        }
        return ProcessedPost(title, type, new_content_url = contentUri)
    }

    private suspend fun parsePost(info: PostInfo): ProcessedPost? {
        val title = info.title
        val contentUri: Uri
        val type: PostType
        val body: String?
        val post: ProcessedPost
        if (title == null) {
            return null
        }
        when {
            info.isSelf != null && info.isSelf!! -> {
                body = info.selftext
                type = PostType.TEXT
                post = ProcessedPost(title, type, body)
            }
            info.postHint == "image" -> {
                contentUri = Uri.parse(info.url)
                type = PostType.IMAGE
                post = ProcessedPost(title, type, new_content_url = contentUri)
            }
            info.secureMedia != null && info.secureMedia?.redditVideo != null -> {
                contentUri = Uri.parse(info.secureMedia?.redditVideo?.dashUrl)
                type = PostType.VIDEO_DASH
                post = ProcessedPost(title, type, new_content_url = contentUri)
            }
            info.media != null && info.media?.redditVideo != null -> {
                contentUri = Uri.parse(info.media?.redditVideo?.dashUrl)
                type = PostType.VIDEO_DASH
                post = ProcessedPost(title, type, new_content_url = contentUri)
            }
            info.domain != null && "imgur" in info.domain!! -> {
                post = parseImgurImage(title, info.url!!) ?: (parseImgurAlbum(title, info.url!!) ?: return null)
            }
            info.domain != null && "gfycat" in info.domain!! -> {
                post = parseGfy(title, info.url!!) ?: return null
            }
            info.preview != null && info.preview?.redditVideoPreview != null -> {
                contentUri = Uri.parse(info.preview?.redditVideoPreview?.dashUrl)
                type = PostType.VIDEO_DASH
                post = ProcessedPost(title, type, new_content_url = contentUri)
            }
            else -> {
                contentUri = Uri.parse(info.url)
                type = PostType.URL
                post = ProcessedPost(title, type, new_content_url = contentUri)
            }
        }
        return post
    }

    private suspend fun getMyFrontPagePosts(): PostPage? {
        val token = getRedditToken() ?: return null
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getredditService(token)
        }
        val res = reddit!!.getMyFrontPagePosts()
        if (!res.isSuccessful) return null
        val posts = res.body()?.data?.children!!
        val processed = posts.map { info -> parsePost(info.data!!) }
        return PostPage(processed, res.body()?.data?.after)
    }

    private suspend fun getMyFrontPagePosts(after: String?, count: Int): PostPage? {
        val token = getRedditToken() ?: return null
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getredditService(token)
        }
        val res = reddit!!.getMyFrontPagePosts(after, count)
        if (!res.isSuccessful) return null
        val posts = res.body()?.data?.children!!
        val processed = posts.map { info -> parsePost(info.data!!) }
        return PostPage(processed, res.body()?.data?.after)
    }

    private suspend fun getMyMultiPosts(name: String): PostPage? {
        val token = getRedditToken() ?: return null
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getredditService(token)
        }
        val res = reddit!!.getMyMultiPosts(name)
        if (!res.isSuccessful) return null
        val posts = res.body()?.data?.children!!
        val processed = posts.map { info -> parsePost(info.data!!) }
        return PostPage(processed, res.body()?.data?.after)
    }

    private suspend fun getMyMultiPosts(name: String, after: String?, count: Int): PostPage? {
        val token = getRedditToken() ?: return null
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getredditService(token)
        }
        val res = reddit!!.getMyMultiPosts(name, after, count)
        if (!res.isSuccessful) return null
        val posts = res.body()?.data?.children!!
        val processed = posts.map { info -> parsePost(info.data!!) }
        return PostPage(processed, res.body()?.data?.after)
    }

    private suspend fun getSubredditPosts(name: String): PostPage? {
        val token = getRedditToken() ?: return null
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getredditService(token)
        }
        val res = reddit!!.getSubredditPosts(name)
        if (!res.isSuccessful) return null
        val posts = res.body()?.data?.children!!
        val processed = posts.map { info -> parsePost(info.data!!) }
        return PostPage(processed, res.body()?.data?.after)
    }

    private suspend fun getSubredditPosts(name: String, after: String?, count: Int): PostPage? {
        val token = getRedditToken() ?: return null
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getredditService(token)
        }
        val res = reddit!!.getSubredditPosts(name, after, count)
        if (!res.isSuccessful) return null
        val posts = res.body()?.data?.children!!
        val processed = posts.map { info -> parsePost(info.data!!) }
        return PostPage(processed, res.body()?.data?.after)
    }

    private suspend fun getMyMultis(): List<String?>? {
        val token = getRedditToken() ?: return null
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getredditService(token)
        }
        val res = reddit!!.getMyMultis()
        if (!res.isSuccessful) return null
        val multis = res.body()!!
        return multis.map { info -> info.data?.displayName }
    }

    private suspend fun getMySubscribedSubreddits(): List<String?>? {
        val token = getRedditToken() ?: return null
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getredditService(token)
        }
        var res = reddit!!.getMySubscribedSubreddits()
        if (!res.isSuccessful) return null
        val names = ArrayList<String?>()
        var subreddits = res.body()?.data?.children!!
        for (subreddit in subreddits) {
            names.add(subreddit.data?.displayName)
        }
        var after = res.body()?.data?.after
        var count = res.body()?.data?.dist!!
        while (after != null) {
            res = reddit!!.getMySubscribedSubreddits(after, count)
            if (!res.isSuccessful) return null
            subreddits = res.body()?.data?.children!!
            for (subreddit in subreddits) {
                names.add(subreddit.data?.displayName)
            }
            after = res.body()?.data?.after
            count += res.body()?.data?.dist!!
        }
        return names
    }


    fun getMyFrontPagePosts(listener: Listener<PostPage?>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMyFrontPagePosts())
        }
    }

    fun getMyFrontPagePosts(after: String?, count: Int, listener: Listener<PostPage?>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMyFrontPagePosts(after, count))
        }
    }

    fun getMyMultiPosts(name: String, listener: Listener<PostPage?>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMyMultiPosts(name))
        }
    }

    fun getMyMultiPosts(name: String, after: String?, count: Int, listener: Listener<PostPage?>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMyMultiPosts(name, after, count))
        }
    }

    fun getSubredditPosts(name: String, listener: Listener<PostPage?>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getSubredditPosts(name))
        }
    }

    fun getSubredditPosts(name: String, after: String?, count: Int, listener: Listener<PostPage?>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getSubredditPosts(name, after, count))
        }
    }

    fun getMyMultis(listener: Listener<List<String?>?>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMyMultis())
        }
    }

    fun getMySubscribedSubreddits(listener: Listener<List<String?>?>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMySubscribedSubreddits())
        }
    }

    interface Listener<T> {
        fun onComplete(result: T)
    }

}
