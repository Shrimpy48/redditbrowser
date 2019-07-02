package com.example.redditbrowser.apis

import android.util.Log
import com.example.redditbrowser.apis.responses.GfyAuthRequest
import com.example.redditbrowser.apis.responses.PostInfo
import com.example.redditbrowser.apis.responses.SelfInfo
import com.example.redditbrowser.apis.services.GfyApiService
import com.example.redditbrowser.apis.services.ImgurApiService
import com.example.redditbrowser.apis.services.RedditApiService
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.datastructs.Post
import kotlinx.coroutines.*
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


    private suspend fun getRedditToken(): String {
        redditMutex.withLock {
            if (redditToken != null && System.currentTimeMillis() < redditExpireTime!!) {
                return redditToken!!
            }
            val authResp = redditAuth.getAuth("password", AuthValues.redditUsername, AuthValues.redditPassword)
            return if (authResp.isSuccessful) {
                redditToken = authResp.body()?.accessToken!!
                redditExpireTime = System.currentTimeMillis() + (authResp.body()?.expiresIn!! * 1000)
                redditToken!!
            } else {
                Log.e("Reddit auth", authResp.message())
                throw Exception("Reddit authentication failed")
            }
        }
    }

    private suspend fun getGfyToken(): String {
        gfyMutex.withLock {
            if (gfyToken != null && System.currentTimeMillis() < gfyExpireTime!!) {
                return gfyToken!!
            }
            val request = GfyAuthRequest()
            request.grantType = "client_credentials"
            request.clientId = AuthValues.gfyId
            request.clientSecret = AuthValues.gfySecret
            val authResp = gfyAuth.getAuth(request)
            return if (authResp.isSuccessful) {
                gfyToken = authResp.body()?.accessToken
                gfyExpireTime = System.currentTimeMillis() + (authResp.body()?.expiresIn!! * 1000)
                gfyToken!!
            } else {
                Log.e("Gfy auth", authResp.message())
                throw Exception("Gfy authentication failed")
            }
        }
    }

    private suspend fun parseImgurImage(
        name: String,
        title: String,
        subreddit: String,
        author: String,
        isNsfw: Boolean,
        url: String
    ): Post? {
        val id = url.substringAfterLast("/").substringBeforeLast(".")
        var service: ImgurApiService? = null
        imgurServiceMutex.withLock {
            service = ServiceGenerator.getImgurService()
        }
        val res = service!!.getImage(id)
        if (res.isSuccessful) {
            val contentUrl: String?
            val type: Int
            val width: Int?
            val height: Int?
            when {
                res.body()?.data?.mp4 != null -> {
                    contentUrl = res.body()?.data?.mp4
                    type = Post.VIDEO
                    width = res.body()?.data?.width
                    height = res.body()?.data?.height
                }
                res.body()?.data?.link != null -> {
                    contentUrl = res.body()?.data?.link
                    type = Post.IMAGE
                    width = res.body()?.data?.width
                    height = res.body()?.data?.height
                }
                else -> {
                    contentUrl = url
                    type = Post.URL
                    width = null
                    height = null
                }
            }
            return Post(
                name,
                title,
                author,
                subreddit,
                isNsfw,
                type,
                url = contentUrl,
                width = width,
                height = height
            )
        }
        Log.d("Imgur image", "ID $id not successfully fetched (code ${res.code()})")
        return null
    }

    private suspend fun parseImgurAlbum(
        name: String,
        title: String,
        subreddit: String,
        author: String,
        isNsfw: Boolean,
        url: String
    ): Post? {
        val id = url.substringAfterLast("/").substringBeforeLast(".")
        var service: ImgurApiService? = null
        imgurServiceMutex.withLock {
            service = ServiceGenerator.getImgurService()
        }
        val res = service!!.getAlbumImages(id)
        if (res.isSuccessful) {
            // TODO fully handle albums
            val contentUrl: String?
            val type: Int
            val width: Int?
            val height: Int?
            when {
                res.body()?.data.isNullOrEmpty() -> throw Exception("No content")

                res.body()?.data!![0].mp4 != null -> {
                    contentUrl = res.body()?.data!![0].mp4
                    type = Post.VIDEO
                    width = res.body()?.data!![0].width
                    height = res.body()?.data!![0].height
                }
                res.body()?.data!![0].link != null -> {
                    contentUrl = res.body()?.data!![0].link
                    type = Post.IMAGE
                    width = res.body()?.data!![0].width
                    height = res.body()?.data!![0].height
                }
                else -> {
                    contentUrl = url
                    type = Post.URL
                    width = null
                    height = null
                }
            }
            return Post(
                name,
                title,
                author,
                subreddit,
                isNsfw,
                type,
                url = contentUrl,
                width = width,
                height = height
            )
        }
        Log.d("Imgur album", "ID $id not successfully fetched (code ${res.code()})")
        return null
    }

    private suspend fun parseGfy(
        name: String,
        title: String,
        subreddit: String,
        author: String,
        isNsfw: Boolean,
        url: String
    ): Post? {
        val id = url.substringAfterLast("/").substringBeforeLast(".").substringBefore("-")
        val token = getGfyToken()
        val contentUrl: String?
        val type: Int
        val width: Int?
        val height: Int?
        var service: GfyApiService? = null
        gfyServiceMutex.withLock {
            service = ServiceGenerator.getGfyService(token)
        }
        val res = service!!.getGfycat(id)
        if (res.isSuccessful) {
            contentUrl = res.body()?.gfyItem?.mp4Url
            type = Post.VIDEO
            width = res.body()?.gfyItem?.width
            height = res.body()?.gfyItem?.height
        } else {
            Log.d("Gfy", "ID $id not successfully fetched (code ${res.code()})")
            return null
        }
        return Post(
            name,
            title,
            author,
            subreddit,
            isNsfw,
            type,
            url = contentUrl,
            width = width,
            height = height
        )
    }

    private suspend fun parsePost(info: PostInfo): Post {
        val title = info.title
        val subreddit = info.subreddit
        val name = info.name
        val author = info.author
        val isNsfw = info.over18
        val contentUrl: String?
        val width: Int?
        val height: Int?
        val type: Int
        if (title == null || subreddit == null || name == null || author == null || isNsfw == null) {
            throw Exception("No data")
        }
        if (info.isSelf != null && info.isSelf!!) {
            val body = info.selftext
            type = Post.TEXT
            return Post(
                name,
                title,
                author,
                subreddit,
                isNsfw,
                type,
                selftext = body
            )
        }
        if (info.domain != null && "imgur" in info.domain!!) {
            val post = parseImgurImage(name, title, subreddit, author, isNsfw, info.url!!)
                ?: parseImgurAlbum(name, title, subreddit, author, isNsfw, info.url!!)
            if (post != null) return post
        }
        if (info.domain != null && "gfycat" in info.domain!!) {
            val post = parseGfy(name, title, subreddit, author, isNsfw, info.url!!)
            if (post != null) return post
        }
        if (info.postHint == "image") {
            contentUrl = info.url
            width = info.preview?.images!![0].source?.width
            height = info.preview?.images!![0].source?.height
            type = Post.IMAGE
            return Post(
                name,
                title,
                author,
                subreddit,
                isNsfw,
                type,
                url = contentUrl,
                width = width,
                height = height
            )
        }
        if (info.secureMedia != null && info.secureMedia?.redditVideo != null) {
            contentUrl = info.secureMedia?.redditVideo?.dashUrl
            width = info.secureMedia?.redditVideo?.width
            height = info.secureMedia?.redditVideo?.height
            type = Post.DASH
            return Post(
                name,
                title,
                author,
                subreddit,
                isNsfw,
                type,
                url = contentUrl,
                width = width,
                height = height
            )
        }
        if (info.media != null && info.media?.redditVideo != null) {
            contentUrl = info.media?.redditVideo?.dashUrl
            width = info.media?.redditVideo?.width
            height = info.media?.redditVideo?.height
            type = Post.DASH
            return Post(
                name,
                title,
                author,
                subreddit,
                isNsfw,
                type,
                url = contentUrl,
                width = width,
                height = height
            )
        }
        if (info.preview != null && info.preview?.redditVideoPreview != null) {
            contentUrl = info.preview?.redditVideoPreview?.dashUrl
            width = info.preview?.redditVideoPreview?.width
            height = info.preview?.redditVideoPreview?.height
            type = Post.DASH
            return Post(
                name,
                title,
                author,
                subreddit,
                isNsfw,
                type,
                url = contentUrl,
                width = width,
                height = height
            )
        }
        contentUrl = info.url
        type = Post.URL
        return Post(
            name,
            title,
            author,
            subreddit,
            isNsfw,
            type,
            url = contentUrl
        )
    }

    private suspend fun getMyFrontPagePosts(): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyFrontPagePosts()
        if (!res.isSuccessful) throw Exception("Unable to fetch front page")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getMyFrontPagePosts(after: String?, count: Int): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyFrontPagePosts(after, count)
        if (!res.isSuccessful) throw Exception("Unable to fetch front page")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getMyFrontPagePosts(limit: Int): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyFrontPagePosts(limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch front page")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getMyFrontPagePosts(after: String?, count: Int, limit: Int): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyFrontPagePosts(after, count, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch front page")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getMyMultiPosts(name: String): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyMultiPosts(name)
        if (!res.isSuccessful) throw Exception("Unable to fetch multi $name")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getMyMultiPosts(name: String, after: String?, count: Int): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyMultiPosts(name, after, count)
        if (!res.isSuccessful) throw Exception("Unable to fetch multi $name")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getMyMultiPosts(name: String, limit: Int): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyMultiPosts(name, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch multi $name")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getMyMultiPosts(name: String, after: String?, count: Int, limit: Int): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyMultiPosts(name, after, count, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch multi $name")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getSubredditPosts(name: String): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getSubredditPosts(name)
        if (!res.isSuccessful) throw Exception("Unable to fetch subreddit $name")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getSubredditPosts(name: String, after: String?, count: Int): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getSubredditPosts(name, after, count)
        if (!res.isSuccessful) throw Exception("Unable to fetch subreddit $name")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getSubredditPosts(name: String, limit: Int): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getSubredditPosts(name, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch subreddit $name")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getSubredditPosts(name: String, after: String?, count: Int, limit: Int): List<Post> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getSubredditPosts(name, after, count, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch subreddit $name")
        val posts = res.body()?.data?.children!!
        return posts.pmap { info -> parsePost(info.data!!) }
    }

    private suspend fun getMyMultis(): List<String> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyMultis()
        if (!res.isSuccessful) throw Exception("Unable to fetch multis")
        val multis = res.body()!!
        return multis.map { info -> info.data?.displayName!! }
    }

    private suspend fun getMySubscribedSubreddits(): List<String> {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        var res = reddit!!.getMySubscribedSubreddits(100)
        if (!res.isSuccessful) throw Exception("Unable to fetch subreddits")
        val names = ArrayList<String>()
        var subreddits = res.body()?.data?.children!!
        for (subreddit in subreddits) {
            names.add(subreddit.data?.displayName!!)
        }
        var after = res.body()?.data?.after
        var count = res.body()?.data?.dist!!
        while (after != null) {
            res = reddit!!.getMySubscribedSubreddits(after, count, 100)
            if (!res.isSuccessful) throw Exception("Unable to fetch subreddits")
            subreddits = res.body()?.data?.children!!
            for (subreddit in subreddits) {
                names.add(subreddit.data?.displayName!!)
            }
            after = res.body()?.data?.after
            count += res.body()?.data?.dist!!
        }
        return names
    }

    suspend fun getMyInfo(): SelfInfo {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyInfo()
        if (!res.isSuccessful) throw Exception("Unable to fetch info")
        return res.body()!!
    }

    suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
        map { async { f(it) } }.map { it.await() }
    }


    fun getMyFrontPagePosts(listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getMyFrontPagePosts()
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyFrontPagePosts(after: String?, count: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getMyFrontPagePosts(after, count)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyFrontPagePosts(limit: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getMyFrontPagePosts(limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyFrontPagePosts(after: String?, count: Int, limit: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getMyFrontPagePosts(after, count, limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyMultiPosts(name: String, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getMyMultiPosts(name)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyMultiPosts(name: String, after: String?, count: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getMyMultiPosts(name, after, count)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyMultiPosts(name: String, limit: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getMyMultiPosts(name, limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyMultiPosts(name: String, after: String?, count: Int, limit: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getMyMultiPosts(name, after, count, limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getSubredditPosts(name: String, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getSubredditPosts(name)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getSubredditPosts(name: String, after: String?, count: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getSubredditPosts(name, after, count)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getSubredditPosts(name: String, limit: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getSubredditPosts(name, limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getSubredditPosts(name: String, after: String?, count: Int, limit: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<Post>? = try {
                getSubredditPosts(name, after, count, limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyMultis(listener: Listener<List<String>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<String>? = try {
                getMyMultis()
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMySubscribedSubreddits(listener: Listener<List<String>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: List<String>? = try {
                getMySubscribedSubreddits()
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyInfo(listener: Listener<SelfInfo>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res: SelfInfo? = try {
                getMyInfo()
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getFeedPosts(feed: Feed, listener: Listener<List<Post>>) {
        when {
            feed.feedType == Feed.TYPE_FRONTPAGE ->
                getMyFrontPagePosts(listener)

            feed.feedType == Feed.TYPE_SUBREDDIT ->
                getSubredditPosts(feed.feed, listener)

            feed.feedType == Feed.TYPE_MULTIREDDIT ->
                getMyMultiPosts(feed.feed, listener)

            else -> throw IllegalArgumentException("Invalid feed type")
        }
    }

    fun getFeedPosts(feed: Feed, after: String?, count: Int, listener: Listener<List<Post>>) {
        when {
            feed.feedType == Feed.TYPE_FRONTPAGE ->
                getMyFrontPagePosts(after, count, listener)

            feed.feedType == Feed.TYPE_SUBREDDIT ->
                getSubredditPosts(feed.feed, after, count, listener)

            feed.feedType == Feed.TYPE_MULTIREDDIT ->
                getMyMultiPosts(feed.feed, after, count, listener)

            else -> throw IllegalArgumentException("Invalid feed type")
        }
    }

    fun getFeedPosts(feed: Feed, limit: Int, listener: Listener<List<Post>>) {
        when {
            feed.feedType == Feed.TYPE_FRONTPAGE ->
                getMyFrontPagePosts(limit, listener)

            feed.feedType == Feed.TYPE_SUBREDDIT ->
                getSubredditPosts(feed.feed, limit, listener)

            feed.feedType == Feed.TYPE_MULTIREDDIT ->
                getMyMultiPosts(feed.feed, limit, listener)

            else -> throw IllegalArgumentException("Invalid feed type")
        }
    }

    fun getFeedPosts(feed: Feed, after: String?, count: Int, limit: Int, listener: Listener<List<Post>>) {
        when {
            feed.feedType == Feed.TYPE_FRONTPAGE ->
                getMyFrontPagePosts(after, count, limit, listener)

            feed.feedType == Feed.TYPE_SUBREDDIT ->
                getSubredditPosts(feed.feed, after, count, limit, listener)

            feed.feedType == Feed.TYPE_MULTIREDDIT ->
                getMyMultiPosts(feed.feed, after, count, limit, listener)

            else -> throw IllegalArgumentException("Invalid feed type")
        }
    }

    interface Listener<T> {
        fun onComplete(result: T)

        fun onFailure(t: Throwable)
    }

}
