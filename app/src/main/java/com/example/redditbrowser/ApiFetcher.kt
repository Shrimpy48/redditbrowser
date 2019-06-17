package com.example.redditbrowser

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


object ApiFetcher {

    private val redditAuth = ServiceGenerator.getRedditAuthService()
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

    private suspend fun parseImgurImage(title: String, subreddit: String, url: String): ProcessedPost? {
        val id = url.substringAfterLast("/").substringBeforeLast(".")
        var service: ImgurApiService? = null
        imgurServiceMutex.withLock {
            service = ServiceGenerator.getImgurService()
        }
        val res = service!!.getImage(id)
        if (res.isSuccessful) {
            val contentUri: Uri
            val type: PostType
            val width: Int?
            val height: Int?
            when {
                res.body()?.data?.mp4 != null -> {
                    contentUri = Uri.parse(res.body()?.data?.mp4)
                    type = PostType.VIDEO
                    width = res.body()?.data?.width
                    height = res.body()?.data?.height
                }
                res.body()?.data?.link != null -> {
                    contentUri = Uri.parse(res.body()?.data?.link)
                    type = PostType.IMAGE
                    width = res.body()?.data?.width
                    height = res.body()?.data?.height
                }
                else -> {
                    contentUri = Uri.parse(url)
                    type = PostType.URL
                    width = null
                    height = null
                }
            }
            return ProcessedPost(title, type, subreddit, contentUrl = contentUri, width = width, height = height)
        }
        Log.d("Imgur image", "ID $id not successfully fetched")
        val contentUri = Uri.parse(url)
        val type = PostType.URL
        return ProcessedPost(title, type, subreddit, contentUrl = contentUri)
    }

    private suspend fun parseImgurAlbum(title: String, subreddit: String, url: String): ProcessedPost? {
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
            val width: Int?
            val height: Int?
            when {
                res.body()?.data.isNullOrEmpty() -> return null

                res.body()?.data!![0].mp4 != null -> {
                    contentUri = Uri.parse(res.body()?.data!![0].mp4)
                    type = PostType.VIDEO
                    width = res.body()?.data!![0].width
                    height = res.body()?.data!![0].height
                }
                res.body()?.data!![0].link != null -> {
                    contentUri = Uri.parse(res.body()?.data!![0].link)
                    type = PostType.IMAGE
                    width = res.body()?.data!![0].width
                    height = res.body()?.data!![0].height
                }
                else -> {
                    contentUri = Uri.parse(url)
                    type = PostType.URL
                    width = null
                    height = null
                }
            }
            return ProcessedPost(title, type, subreddit, contentUrl = contentUri, width = width, height = height)
        }
        Log.d("Imgur album", "ID $id not successfully fetched")
        val contentUri = Uri.parse(url)
        val type = PostType.URL
        return ProcessedPost(title, type, subreddit, contentUrl = contentUri)
    }

    private suspend fun parseGfy(title: String, subreddit: String, url: String): ProcessedPost? {
        val id = url.substringAfterLast("/").substringBeforeLast(".").substringBefore("-")
        val token = getGfyToken() ?: return null
        val contentUri: Uri
        val type: PostType
        val width: Int?
        val height: Int?
        var service: GfyApiService? = null
        gfyServiceMutex.withLock {
            service = ServiceGenerator.getGfyService(token)
        }
        val res = service!!.getGfycat(id)
        if (res.isSuccessful) {
            contentUri = Uri.parse(res.body()?.gfyItem?.mp4Url)
            type = PostType.VIDEO
            width = res.body()?.gfyItem?.width
            height = res.body()?.gfyItem?.height
        } else {
            contentUri = Uri.parse(url)
            type = PostType.URL
            Log.d("Gfy", "ID $id not successfully fetched")
            width = null
            height = null
        }
        return ProcessedPost(title, type, subreddit, contentUrl = contentUri, width = width, height = height)
    }

    private suspend fun parsePost(info: PostInfo): ProcessedPost? {
        val title = info.title
        val subreddit = info.subreddit
        val contentUri: Uri
        val width: Int?
        val height: Int?
        val type: PostType
        val post: ProcessedPost
        if (title == null || subreddit == null) {
            return null
        }
        when {
            info.isSelf != null && info.isSelf!! -> {
                val body = info.selftext
                type = PostType.TEXT
                post = ProcessedPost(title, type, subreddit, body = body)
            }
            info.postHint == "image" -> {
                contentUri = Uri.parse(info.url)
                width = info.preview?.images!![0].source?.width
                height = info.preview?.images!![0].source?.height
                type = PostType.IMAGE
                post = ProcessedPost(title, type, subreddit, contentUrl = contentUri, width = width, height = height)
            }
            info.secureMedia != null && info.secureMedia?.redditVideo != null -> {
                contentUri = Uri.parse(info.secureMedia?.redditVideo?.dashUrl)
                width = info.secureMedia?.redditVideo?.width
                height = info.secureMedia?.redditVideo?.height
                type = PostType.VIDEO_DASH
                post = ProcessedPost(title, type, subreddit, contentUrl = contentUri, width = width, height = height)
            }
            info.media != null && info.media?.redditVideo != null -> {
                contentUri = Uri.parse(info.media?.redditVideo?.dashUrl)
                width = info.media?.redditVideo?.width
                height = info.media?.redditVideo?.height
                type = PostType.VIDEO_DASH
                post = ProcessedPost(title, type, subreddit, contentUrl = contentUri, width = width, height = height)
            }
            info.domain != null && "imgur" in info.domain!! -> {
                post = parseImgurImage(title, subreddit, info.url!!) ?: (parseImgurAlbum(title, subreddit, info.url!!)
                    ?: return null)
            }
            info.domain != null && "gfycat" in info.domain!! -> {
                post = parseGfy(title, subreddit, info.url!!) ?: return null
            }
            info.preview != null && info.preview?.redditVideoPreview != null -> {
                contentUri = Uri.parse(info.preview?.redditVideoPreview?.dashUrl)
                width = info.preview?.redditVideoPreview?.width
                height = info.preview?.redditVideoPreview?.height
                type = PostType.VIDEO_DASH
                post = ProcessedPost(title, type, subreddit, contentUrl = contentUri, width = width, height = height)
            }
            else -> {
                contentUri = Uri.parse(info.url)
                type = PostType.URL
                post = ProcessedPost(title, type, subreddit, contentUrl = contentUri)
            }
        }
        return post
    }

    private suspend fun getMyFrontPagePosts(): PostPage? {
        val token = getRedditToken() ?: return null
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
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
            reddit = ServiceGenerator.getRedditService(token)
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
            reddit = ServiceGenerator.getRedditService(token)
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
            reddit = ServiceGenerator.getRedditService(token)
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
            reddit = ServiceGenerator.getRedditService(token)
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
            reddit = ServiceGenerator.getRedditService(token)
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
            reddit = ServiceGenerator.getRedditService(token)
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
            reddit = ServiceGenerator.getRedditService(token)
        }
        var res = reddit!!.getMySubscribedSubreddits(100)
        if (!res.isSuccessful) return null
        val names = ArrayList<String?>()
        var subreddits = res.body()?.data?.children!!
        for (subreddit in subreddits) {
            names.add(subreddit.data?.displayName)
        }
        var after = res.body()?.data?.after
        var count = res.body()?.data?.dist!!
        while (after != null) {
            res = reddit!!.getMySubscribedSubreddits(after, count, 100)
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