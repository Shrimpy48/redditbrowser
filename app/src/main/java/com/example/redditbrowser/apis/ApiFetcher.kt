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
import com.example.redditbrowser.web.HttpClientBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Request
import org.jsoup.Jsoup


object ApiFetcher {

    private const val useVideoScraping = false
    private const val useOtherScraping = false

    private const val scrapingConditions =
        ":not(:contains(logo), :containsData(logo), :contains(header), :containsData(header), :contains(avatar), :containsData(avatar), [class*=logo], [class*=thumb], [class*=avatar], [class*=header], [id*=logo], [id*=thumb], [id*=avatar], [id*=header], [itemprop*=logo], [itemprop*=thumb], [itemprop*=avatar], [itemprop*=header], [src*=logo], [src*=thumb], [src*=avatar], [src*=header])"

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
                Log.e("Reddit auth", "" + authResp.message())
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
                Log.e("Gfy auth", "" + authResp.message())
                throw Exception("Gfy authentication failed")
            }
        }
    }

    private suspend fun parseImgurImage(
        name: String,
        id: String,
        title: String,
        subreddit: String,
        author: String,
        isNsfw: Boolean,
        isSpoiler: Boolean,
        score: Int,
        url: String
    ): Post? {
        val imgurId = url.substringAfterLast("/").substringBeforeLast(".")
        var service: ImgurApiService? = null
        imgurServiceMutex.withLock {
            service = ServiceGenerator.getImgurService()
        }
        val res = service!!.getImage(imgurId)
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
                else -> return null
            }
            return Post(
                name,
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                content = contentUrl,
                postUrl = url,
                width = width,
                height = height
            )
        }
        Log.d("Imgur image", "ID $imgurId not successfully fetched (code ${res.code()})")
        return null
    }

    private suspend fun parseImgurAlbum(
        name: String,
        id: String,
        title: String,
        subreddit: String,
        author: String,
        isNsfw: Boolean,
        isSpoiler: Boolean,
        score: Int,
        url: String
    ): Post? {
        val imgurId = url.substringAfterLast("/").substringBeforeLast(".").substringBefore("?")
        var service: ImgurApiService? = null
        imgurServiceMutex.withLock {
            service = ServiceGenerator.getImgurService()
        }
        val res = service!!.getAlbumImages(imgurId)
        if (res.isSuccessful) {
            // TODO fully handle albums
            val contentUrl: String?
            val type: Int
            val width: Int?
            val height: Int?
            when {
                res.body()?.data.isNullOrEmpty() -> return null

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
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                content = contentUrl,
                postUrl = url,
                width = width,
                height = height
            )
        }
        Log.d("Imgur album", "ID $imgurId not successfully fetched (code ${res.code()})")
        return null
    }

    private suspend fun parseGfy(
        name: String,
        id: String,
        title: String,
        subreddit: String,
        author: String,
        isNsfw: Boolean,
        isSpoiler: Boolean,
        score: Int,
        url: String
    ): Post? {
        val gfyId = url.substringAfterLast("/").substringBeforeLast(".").substringBefore("?").substringBefore("-")
        val token = getGfyToken()
        val contentUrl: String?
        val type: Int
        val width: Int?
        val height: Int?
        var service: GfyApiService? = null
        gfyServiceMutex.withLock {
            service = ServiceGenerator.getGfyService(token)
        }
        val res = service!!.getGfycat(gfyId)
        if (res.isSuccessful) {
            contentUrl = res.body()?.gfyItem?.mp4Url ?: res.body()?.gfyItem?.webmUrl
            type = Post.VIDEO
            width = res.body()?.gfyItem?.width
            height = res.body()?.gfyItem?.height
        } else {
            Log.d("Gfy", "ID $gfyId not successfully fetched (code ${res.code()})")
            return null
        }
        return Post(
            name,
            id,
            title,
            author,
            subreddit,
            isNsfw,
            isSpoiler,
            type,
            score,
            content = contentUrl,
            postUrl = url,
            width = width,
            height = height
        )
    }

    private fun getHtml(url: String): String? {
        val request = Request.Builder().url(url).get().build()
        val response = HttpClientBuilder.getClient().newCall(request).execute()
        if (!response.isSuccessful) {
            Log.d("Parser", "Failed to fetch HTML")
            return null
        }
        return response.body()?.string()
    }

    private suspend fun scrapeUrl(
        name: String,
        id: String,
        title: String,
        subreddit: String,
        author: String,
        isNsfw: Boolean,
        isSpoiler: Boolean,
        score: Int,
        url: String
    ): Post? = withContext(Dispatchers.IO) {

        if (!(useVideoScraping || useOtherScraping)) return@withContext null

        Log.d("Parser", "Scraping $url")

        val html = getHtml(url)
        val doc = Jsoup.parse(html)

        if (useVideoScraping) {
            val video = doc.selectFirst("video$scrapingConditions")
            if (video != null) {
                Log.d("Parser", "Fetched video")
                val type = Post.VIDEO
                Log.d("Parser", "-> ${video.outerHtml()}")
                val sources = video.getElementsByTag("source")
                val contentUrl = sources.first().attr("abs:src")
                val width = video.attr("width").toIntOrNull()
                val height = video.attr("height").toIntOrNull()
                return@withContext Post(
                    name,
                    id,
                    title,
                    author,
                    subreddit,
                    isNsfw,
                    isSpoiler,
                    type,
                    score,
                    content = contentUrl,
                    postUrl = url,
                    width = width,
                    height = height
                )
            }
        }
        if (useOtherScraping) {
            val embed = doc.selectFirst("meta[property=og:video:url]")
            if (embed != null) {
                Log.d("Parser", "Fetched embed")
                Log.d("Parser", "-> ${embed.outerHtml()}")
                val width = doc.selectFirst("meta[property=og:video:width]")
                    ?.attr("content")
                    ?.toIntOrNull()
                val height = doc.selectFirst("meta[property=og:video:height]")
                    ?.attr("content")
                    ?.toIntOrNull()
                val contentUrl = embed.attr("content")
                val type = Post.EMBED
                return@withContext Post(
                    name,
                    id,
                    title,
                    author,
                    subreddit,
                    isNsfw,
                    isSpoiler,
                    type,
                    score,
                    content = contentUrl,
                    postUrl = url,
                    width = width,
                    height = height
                )
            }
            var embedFrame = html?.substringAfter("embedCode\":\"", "")
                ?.substringBefore("<\\/iframe>")
            if (embedFrame != null && embedFrame.isNotEmpty()) {
                embedFrame += "<\\/iframe>"
                Log.d("Parser", "Fetched embedCode")
                Log.d("Parser", "-> $embedFrame")
                val type = Post.EMBED_HTML
                return@withContext Post(
                    name,
                    id,
                    title,
                    author,
                    subreddit,
                    isNsfw,
                    isSpoiler,
                    type,
                    score,
                    content = embedFrame,
                    postUrl = url
                )
            }
            val image = doc.selectFirst("img$scrapingConditions")
            if (image != null) {
                Log.d("Parser", "Fetched image")
                val type = Post.IMAGE
                Log.d("Parser", "-> ${image.outerHtml()}")
                val contentUrl = image.attr("abs:src")
                val width = image.attr("width")?.toIntOrNull()
                val height = image.attr("height")?.toIntOrNull()
                return@withContext Post(
                    name,
                    id,
                    title,
                    author,
                    subreddit,
                    isNsfw,
                    isSpoiler,
                    type,
                    score,
                    content = contentUrl,
                    postUrl = url,
                    width = width,
                    height = height
                )
            }
        }
        Log.d("Parser", "No content found")
        null
    }

    private suspend fun parsePost(info: PostInfo): Post {
        val title = info.title
        val subreddit = info.subreddit
        val name = info.name
        val id = info.id
        val author = info.author
        val isNsfw = info.over18
        val isSpoiler = info.spoiler
        val score = info.score
        if (title == null) {
            throw Exception("Null title")
        }
        if (subreddit == null) {
            throw Exception("Null subreddit")
        }
        if (name == null) {
            throw Exception("Null name")
        }
        if (id == null) {
            throw Exception("Null id")
        }
        if (author == null) {
            throw Exception("Null author")
        }
        if (isNsfw == null) {
            throw Exception("Null isNsfw")
        }
        if (isSpoiler == null) {
            throw Exception("Null isSpoiler")
        }
        if (score == null) {
            throw Exception("Null score")
        }
        if (info.isSelf != null && info.isSelf!!) {
            val body = info.selftext
            val type = Post.TEXT
            return Post(
                name,
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                selftext = body
            )
        }
        if (info.domain != null && "imgur" in info.domain!!) {
            val post = parseImgurImage(name, id, title, subreddit, author, isNsfw, isSpoiler, score, info.url!!)
                ?: parseImgurAlbum(name, id, title, subreddit, author, isNsfw, isSpoiler, score, info.url!!)
            if (post != null) return post
        }
        if (info.domain != null && "gfycat" in info.domain!!) {
            val post = parseGfy(name, id, title, subreddit, author, isNsfw, isSpoiler, score, info.url!!)
            if (post != null) return post
        }
        if (info.secureMedia != null && info.secureMedia?.redditVideo != null) {
            val contentUrl = info.secureMedia?.redditVideo?.dashUrl
            val width = info.secureMedia?.redditVideo?.width
            val height = info.secureMedia?.redditVideo?.height
            val type = Post.VIDEO_DASH
            return Post(
                name,
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                content = contentUrl,
                postUrl = info.url,
                width = width,
                height = height
            )
        }
        if (info.media != null && info.media?.redditVideo != null) {
            val contentUrl = info.media?.redditVideo?.dashUrl
            val width = info.media?.redditVideo?.width
            val height = info.media?.redditVideo?.height
            val type = Post.VIDEO_DASH
            return Post(
                name,
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                content = contentUrl,
                postUrl = info.url,
                width = width,
                height = height
            )
        }
        if (info.postHint == "image") {
            val contentUrl = info.url
            val width = info.preview?.images!![0].source?.width
            val height = info.preview?.images!![0].source?.height
            val type = Post.IMAGE
            return Post(
                name,
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                content = contentUrl,
                postUrl = info.url,
                width = width,
                height = height
            )
        }
        if (info.crosspostParentList != null) {
            for (parent in info.crosspostParentList!!) {
                val parsed = parsePost(parent)
                if (parsed.type != Post.URL) return parsed
            }
        }
        if (info.preview != null && info.preview?.redditVideoPreview != null) {
            val contentUrl = info.preview?.redditVideoPreview?.dashUrl
            val width = info.preview?.redditVideoPreview?.width
            val height = info.preview?.redditVideoPreview?.height
            val type = Post.VIDEO_DASH
            return Post(
                name,
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                content = contentUrl,
                postUrl = info.url,
                width = width,
                height = height
            )
        }
        if (info.url != null) {
            val post = scrapeUrl(name, id, title, subreddit, author, isNsfw, isSpoiler, score, info.url!!)
            if (post != null) return post
        }
        if (info.secureMediaEmbed != null && info.secureMediaEmbed?.content != null) {
            val content = info.secureMediaEmbed?.content
            val width = info.secureMediaEmbed?.width
            val height = info.secureMediaEmbed?.height
            val type = Post.EMBED_HTML
            return Post(
                name,
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                content = content,
                postUrl = info.url,
                width = width,
                height = height
            )
        }
        if (info.mediaEmbed != null && info.mediaEmbed?.content != null) {
            val content = info.mediaEmbed?.content
            val width = info.mediaEmbed?.width
            val height = info.mediaEmbed?.height
            val type = Post.EMBED_HTML
            return Post(
                name,
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                content = content,
                postUrl = info.url,
                width = width,
                height = height
            )
        }
        if (info.secureMediaEmbed != null && info.secureMediaEmbed?.mediaDomainUrl != null) {
            val contentUrl = info.secureMediaEmbed?.mediaDomainUrl
            val width = info.secureMediaEmbed?.width
            val height = info.secureMediaEmbed?.height
            val type = Post.EMBED
            return Post(
                name,
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                content = contentUrl,
                postUrl = info.url,
                width = width,
                height = height
            )
        }
        if (info.mediaEmbed != null && info.mediaEmbed?.mediaDomainUrl != null) {
            val contentUrl = info.mediaEmbed?.mediaDomainUrl
            val width = info.mediaEmbed?.width
            val height = info.mediaEmbed?.height
            val type = Post.EMBED
            return Post(
                name,
                id,
                title,
                author,
                subreddit,
                isNsfw,
                isSpoiler,
                type,
                score,
                content = contentUrl,
                postUrl = info.url,
                width = width,
                height = height
            )
        }
        Log.d("Parser", "no media found for ${info.name}")
        val contentUrl = info.url
        val type = Post.URL
        return Post(
            name,
            id,
            title,
            author,
            subreddit,
            isNsfw,
            isSpoiler,
            type,
            score,
            content = contentUrl,
            postUrl = info.url
        )
    }

    suspend fun getMyFrontPagePosts(sort: String, period: String, limit: Int): Page<Post> =
        withContext(Dispatchers.IO) {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = if (sort != "") {
            if (period != "") reddit!!.getMyFrontPagePosts(sort, period, limit)
            else reddit!!.getMyFrontPagePosts(sort, limit)
        } else reddit!!.getMyFrontPagePosts(limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch front page")
        val posts = res.body()?.data?.children!!
        val processed = posts.pmap { info -> parsePost(info.data!!) }
        val before = res.body()?.data?.before
        val after = res.body()?.data?.after
        val count = res.body()?.data?.dist!!
            Page(processed, before, after, count)
    }

    suspend fun getMyFrontPagePosts(
        sort: String,
        period: String,
        after: String?,
        count: Int,
        limit: Int
    ): Page<Post> = withContext(Dispatchers.IO) {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = if (sort != "") {
            if (period != "") reddit!!.getMyFrontPagePosts(sort, period, after, count, limit)
            else reddit!!.getMyFrontPagePosts(sort, after, count, limit)
        } else reddit!!.getMyFrontPagePosts(after, count, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch front page")
        val posts = res.body()?.data?.children!!
        val processed = posts.pmap { info -> parsePost(info.data!!) }
        val before = res.body()?.data?.before
        val newAfter = res.body()?.data?.after
        val newCount = count + res.body()?.data?.dist!!
        Page(processed, before, newAfter, newCount)
    }

    suspend fun getMyMultiPosts(name: String, sort: String, period: String, limit: Int): Page<Post> =
        withContext(Dispatchers.IO) {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = if (sort != "") {
            if (period != "") reddit!!.getMyMultiPosts(name, sort, period, limit)
            else reddit!!.getMyMultiPosts(name, sort, limit)
        } else reddit!!.getMyMultiPosts(name, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch multi $name")
        val posts = res.body()?.data?.children!!
        val processed = posts.pmap { info -> parsePost(info.data!!) }
        val before = res.body()?.data?.before
        val after = res.body()?.data?.after
        val count = res.body()?.data?.dist!!
            Page(processed, before, after, count)
    }

    suspend fun getMyMultiPosts(
        name: String,
        sort: String,
        period: String,
        after: String?,
        count: Int,
        limit: Int
    ): Page<Post> = withContext(Dispatchers.IO) {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = if (sort != "") {
            if (period != "") reddit!!.getMyMultiPosts(name, sort, period, after, count, limit)
            else reddit!!.getMyMultiPosts(name, sort, after, count, limit)
        } else reddit!!.getMyMultiPosts(name, after, count, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch multi $name")
        val posts = res.body()?.data?.children!!
        val processed = posts.pmap { info -> parsePost(info.data!!) }
        val before = res.body()?.data?.before
        val newAfter = res.body()?.data?.after
        val newCount = count + res.body()?.data?.dist!!
        Page(processed, before, newAfter, newCount)
    }

    suspend fun getSubredditPosts(name: String, sort: String, period: String, limit: Int): Page<Post> =
        withContext(Dispatchers.IO) {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = if (sort != "") {
            if (period != "") reddit!!.getSubredditPosts(name, sort, period, limit)
            else reddit!!.getSubredditPosts(name, sort, limit)
        } else reddit!!.getSubredditPosts(name, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch subreddit $name")
        val posts = res.body()?.data?.children!!
        val processed = posts.pmap { info -> parsePost(info.data!!) }
        val before = res.body()?.data?.before
        val after = res.body()?.data?.after
        val count = res.body()?.data?.dist!!
            Page(processed, before, after, count)
    }

    suspend fun getSubredditPosts(
        name: String,
        sort: String,
        period: String,
        after: String?,
        count: Int,
        limit: Int
    ): Page<Post> = withContext(Dispatchers.IO) {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = if (sort != "") {
            if (period != "") reddit!!.getSubredditPosts(name, sort, period, after, count, limit)
            else reddit!!.getSubredditPosts(name, sort, after, count, limit)
        } else reddit!!.getSubredditPosts(name, after, count, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch subreddit $name")
        val posts = res.body()?.data?.children!!
        val processed = posts.pmap { info -> parsePost(info.data!!) }
        val before = res.body()?.data?.before
        val newAfter = res.body()?.data?.after
        val newCount = count + res.body()?.data?.dist!!
        Page(processed, before, newAfter, newCount)
    }

    suspend fun getMyMultis(): List<String> = withContext(Dispatchers.IO) {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyMultis()
        if (!res.isSuccessful) throw Exception("Unable to fetch multis")
        val multis = res.body()!!
        multis.map { info -> info.data?.displayName!! }
    }

    suspend fun getMySubscribedSubreddits(limit: Int): Page<String> = withContext(Dispatchers.IO) {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMySubscribedSubreddits(limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch subreddits")
        val subreddits = res.body()?.data?.children!!
        val names = subreddits.map { info -> info.data?.displayName!! }
        val before = res.body()?.data?.before
        val after = res.body()?.data?.after
        val count = res.body()?.data?.dist!!
        Page(names, before, after, count)
    }

    suspend fun getMySubscribedSubreddits(after: String?, count: Int, limit: Int): Page<String> =
        withContext(Dispatchers.IO) {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMySubscribedSubreddits(after, count, limit)
        if (!res.isSuccessful) throw Exception("Unable to fetch subreddits")
        val subreddits = res.body()?.data?.children!!
        val names = subreddits.map { info -> info.data?.displayName!! }
        val before = res.body()?.data?.before
        val newAfter = res.body()?.data?.after
        val newCount = count + res.body()?.data?.dist!!
            Page(names, before, newAfter, newCount)
    }

    suspend fun getMyInfo(): SelfInfo = withContext(Dispatchers.IO) {
        val token = getRedditToken()
        var reddit: RedditApiService? = null
        redditServiceMutex.withLock {
            reddit = ServiceGenerator.getRedditService(token)
        }
        val res = reddit!!.getMyInfo()
        if (!res.isSuccessful) throw Exception("Unable to fetch info")
        res.body()!!
    }

    suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
        map { async { f(it) } }.map { it.await() }
    }


    fun getMyFrontPagePosts(sort: String, period: String, limit: Int, listener: Listener<Page<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res = try {
                getMyFrontPagePosts(sort, period, limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyFrontPagePosts(
        sort: String,
        period: String,
        after: String?,
        count: Int,
        limit: Int,
        listener: Listener<Page<Post>>
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val res = try {
                getMyFrontPagePosts(sort, period, after, count, limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyMultiPosts(name: String, sort: String, period: String, limit: Int, listener: Listener<Page<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res = try {
                getMyMultiPosts(name, sort, period, limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMyMultiPosts(
        name: String,
        sort: String,
        period: String,
        after: String?,
        count: Int,
        limit: Int,
        listener: Listener<Page<Post>>
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val res = try {
                getMyMultiPosts(name, sort, period, after, count, limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getSubredditPosts(name: String, sort: String, period: String, limit: Int, listener: Listener<Page<Post>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res = try {
                getSubredditPosts(name, sort, period, limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getSubredditPosts(
        name: String,
        sort: String,
        period: String,
        after: String?,
        count: Int,
        limit: Int,
        listener: Listener<Page<Post>>
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val res = try {
                getSubredditPosts(name, sort, period, after, count, limit)
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

    fun getMySubscribedSubreddits(limit: Int, listener: Listener<Page<String>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res = try {
                getMySubscribedSubreddits(limit)
            } catch (t: Throwable) {
                listener.onFailure(t)
                null
            }
            if (res != null) listener.onComplete(res)
        }
    }

    fun getMySubscribedSubreddits(after: String?, count: Int, limit: Int, listener: Listener<Page<String>>) {
        CoroutineScope(Dispatchers.Main).launch {
            val res = try {
                getMySubscribedSubreddits(after, count, limit)
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

    fun getFeedPosts(feed: Feed, limit: Int, listener: Listener<Page<Post>>) {
        when {
            feed.feedType == Feed.TYPE_FRONTPAGE ->
                getMyFrontPagePosts(feed.sort, feed.period, limit, listener)

            feed.feedType == Feed.TYPE_SUBREDDIT ->
                getSubredditPosts(feed.feed, feed.sort, feed.period, limit, listener)

            feed.feedType == Feed.TYPE_MULTIREDDIT ->
                getMyMultiPosts(feed.feed, feed.sort, feed.period, limit, listener)

            else -> throw IllegalArgumentException("Invalid feed type")
        }
    }

    fun getFeedPosts(feed: Feed, after: String?, count: Int, limit: Int, listener: Listener<Page<Post>>) {
        when {
            feed.feedType == Feed.TYPE_FRONTPAGE ->
                getMyFrontPagePosts(feed.sort, feed.period, after, count, limit, listener)

            feed.feedType == Feed.TYPE_SUBREDDIT ->
                getSubredditPosts(feed.feed, feed.sort, feed.period, after, count, limit, listener)

            feed.feedType == Feed.TYPE_MULTIREDDIT ->
                getMyMultiPosts(feed.feed, feed.sort, feed.period, after, count, limit, listener)

            else -> throw IllegalArgumentException("Invalid feed type")
        }
    }

    interface Listener<T> {
        fun onComplete(result: T)

        fun onFailure(t: Throwable)
    }

    data class Page<T>(val items: List<T>, val before: String?, val after: String?, val count: Int)

}
