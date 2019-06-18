package com.example.redditbrowser.apis

import android.net.Uri
import android.util.Log
import com.example.redditbrowser.apis.responses.GfyAuthRequest
import com.example.redditbrowser.apis.responses.PostInfo
import com.example.redditbrowser.apis.services.GfyApiService
import com.example.redditbrowser.apis.services.ImgurApiService
import com.example.redditbrowser.apis.services.RedditApiService
import com.example.redditbrowser.datastructs.Feed
import com.example.redditbrowser.datastructs.Post
import com.example.redditbrowser.datastructs.PostType
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
        url: String
    ): Post? {
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
            return Post(
                name,
                title,
                author,
                subreddit,
                type,
                url = contentUri,
                width = width,
                height = height
            )
        }
        Log.d("Imgur image", "ID $id not successfully fetched")
        return null
    }

    private suspend fun parseImgurAlbum(
        name: String,
        title: String,
        subreddit: String,
        author: String,
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
            val contentUri: Uri
            val type: PostType
            val width: Int?
            val height: Int?
            when {
                res.body()?.data.isNullOrEmpty() -> throw Exception("No content")

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
            return Post(
                name,
                title,
                author,
                subreddit,
                type,
                url = contentUri,
                width = width,
                height = height
            )
        }
        Log.d("Imgur album", "ID $id not successfully fetched")
        return null
    }

    private suspend fun parseGfy(name: String, title: String, subreddit: String, author: String, url: String): Post? {
        val id = url.substringAfterLast("/").substringBeforeLast(".").substringBefore("-")
        val token = getGfyToken()
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
            Log.d("Gfy", "ID $id not successfully fetched")
            return null
        }
        return Post(
            name,
            title,
            author,
            subreddit,
            type,
            url = contentUri,
            width = width,
            height = height
        )
    }

    private suspend fun parsePost(info: PostInfo): Post {
        val title = info.title
        val subreddit = info.subreddit
        val name = info.name
        val author = info.author
        val contentUri: Uri
        val width: Int?
        val height: Int?
        val type: PostType
        val post: Post
        if (title == null || subreddit == null || name == null || author == null) {
            throw Exception("No data")
        }
        when {
            info.isSelf != null && info.isSelf!! -> {
                val body = info.selftext
                type = PostType.TEXT
                post = Post(
                    name,
                    title,
                    author,
                    subreddit,
                    type,
                    selftext = body
                )
            }
            info.postHint == "image" -> {
                contentUri = Uri.parse(info.url)
                width = info.preview?.images!![0].source?.width
                height = info.preview?.images!![0].source?.height
                type = PostType.IMAGE
                post = Post(
                    name,
                    title,
                    author,
                    subreddit,
                    type,
                    url = contentUri,
                    width = width,
                    height = height
                )
            }
            info.secureMedia != null && info.secureMedia?.redditVideo != null -> {
                contentUri = Uri.parse(info.secureMedia?.redditVideo?.dashUrl)
                width = info.secureMedia?.redditVideo?.width
                height = info.secureMedia?.redditVideo?.height
                type = PostType.DASH
                post = Post(
                    name,
                    title,
                    author,
                    subreddit,
                    type,
                    url = contentUri,
                    width = width,
                    height = height
                )
            }
            info.media != null && info.media?.redditVideo != null -> {
                contentUri = Uri.parse(info.media?.redditVideo?.dashUrl)
                width = info.media?.redditVideo?.width
                height = info.media?.redditVideo?.height
                type = PostType.DASH
                post = Post(
                    name,
                    title,
                    author,
                    subreddit,
                    type,
                    url = contentUri,
                    width = width,
                    height = height
                )
            }
            info.domain != null && "imgur" in info.domain!! -> {
                post = parseImgurImage(name, title, subreddit, author, info.url!!)
                    ?: (parseImgurAlbum(name, title, subreddit, author, info.url!!)
                        ?: throw Exception("Could not fetch imgur content"))
            }
            info.domain != null && "gfycat" in info.domain!! -> {
                post = parseGfy(name, title, subreddit, author, info.url!!)
                    ?: throw Exception("Could not fetch gfy content")
            }
            info.preview != null && info.preview?.redditVideoPreview != null -> {
                contentUri = Uri.parse(info.preview?.redditVideoPreview?.dashUrl)
                width = info.preview?.redditVideoPreview?.width
                height = info.preview?.redditVideoPreview?.height
                type = PostType.DASH
                post = Post(
                    name,
                    title,
                    author,
                    subreddit,
                    type,
                    url = contentUri,
                    width = width,
                    height = height
                )
            }
            else -> {
                contentUri = Uri.parse(info.url)
                type = PostType.URL
                post = Post(
                    name,
                    title,
                    author,
                    subreddit,
                    type,
                    url = contentUri
                )
            }
        }
        return post
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
        return posts.map { info -> parsePost(info.data!!) }
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
        return posts.map { info -> parsePost(info.data!!) }
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
        return posts.map { info -> parsePost(info.data!!) }
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
        return posts.map { info -> parsePost(info.data!!) }
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
        return posts.map { info -> parsePost(info.data!!) }
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
        return posts.map { info -> parsePost(info.data!!) }
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


    fun getMyFrontPagePosts(listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMyFrontPagePosts())
        }
    }

    fun getMyFrontPagePosts(after: String?, count: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMyFrontPagePosts(after, count))
        }
    }

    fun getMyMultiPosts(name: String, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMyMultiPosts(name))
        }
    }

    fun getMyMultiPosts(name: String, after: String?, count: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMyMultiPosts(name, after, count))
        }
    }

    fun getSubredditPosts(name: String, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getSubredditPosts(name))
        }
    }

    fun getSubredditPosts(name: String, after: String?, count: Int, listener: Listener<List<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getSubredditPosts(name, after, count))
        }
    }

    fun getMyMultis(listener: Listener<List<String>>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMyMultis())
        }
    }

    fun getMySubscribedSubreddits(listener: Listener<List<String>>) {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(getMySubscribedSubreddits())
        }
    }

    fun getFeedPosts(feed: Feed, listener: Listener<List<Post>>) {
        when {
            feed.feedType == Feed.TYPE_SPECIAL && feed.feed == "Front Page" ->
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
            feed.feedType == Feed.TYPE_SPECIAL && feed.feed == "Front Page" ->
                getMyFrontPagePosts(after, count, listener)

            feed.feedType == Feed.TYPE_SUBREDDIT ->
                getSubredditPosts(feed.feed, after, count, listener)

            feed.feedType == Feed.TYPE_MULTIREDDIT ->
                getMyMultiPosts(feed.feed, after, count, listener)

            else -> throw IllegalArgumentException("Invalid feed type")
        }
    }

    interface Listener<T> {
        fun onComplete(result: T)
    }

}
