package com.example.redditbrowser

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.example.redditbrowser.web.HttpClientBuilder
import java.io.InputStream

@GlideModule
class MyGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setLogLevel(Log.DEBUG)
//        builder.apply {
//            RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                .signature(ObjectKey(System.currentTimeMillis().toShort()))
//        }
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        val client = HttpClientBuilder.getClient()
        val factory = OkHttpUrlLoader.Factory(client)

        registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }

    override fun isManifestParsingEnabled(): Boolean = false
}
