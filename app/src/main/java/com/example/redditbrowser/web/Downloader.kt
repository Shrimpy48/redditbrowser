package com.example.redditbrowser.web

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.redditbrowser.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import okio.Okio
import java.io.File


object Downloader {

    private const val downloads_channel = "downloads"

    private var id = 0

    fun download(context: Context, urlStr: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val filename = urlStr.substringAfterLast("/")
                val request = Request.Builder().url(urlStr).build()
                val response = HttpClientBuilder.getClient().newCall(request).execute()
                response.body()?.let {
                    val downloadedFile = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        filename
                    )
                    val sink = Okio.buffer(Okio.sink(downloadedFile))
                    sink.writeAll(it.source())
                    sink.close()
                    makeNotif(context, "$filename downloaded")
                }
            } catch (e: SecurityException) {
                makeNotif(context, "Permission not granted")
            }
        }
//        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        val uri = Uri.parse(urlStr)
//        val request = DownloadManager.Request(uri)
//            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.allowScanningByMediaScanner()
//        downloadManager.enqueue(request)
    }

    private fun createNotifChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.download_channel_name)
            val descriptionText = context.getString(R.string.download_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(downloads_channel, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    private fun makeNotif(context: Context, msg: String) {
        createNotifChannel(context)
        val builder = NotificationCompat.Builder(context, downloads_channel)
            .setSmallIcon(R.drawable.ic_file_download_black_24dp)
            .setContentTitle(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            notify(id++, builder.build())
        }
    }
}