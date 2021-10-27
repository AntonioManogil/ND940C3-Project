package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

// Notification ID.
const val NOTIFICATION_ID = 0
const val DOWNLOAD_FILE = "DOWNLOAD_FILE"
const val DOWNLOAD_STATUS = "DOWNLOAD_STATUS"

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(
    filename: String,
    status: Boolean,
    applicationContext: Context
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(DOWNLOAD_FILE, filename)
    contentIntent.putExtra(DOWNLOAD_STATUS, status)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val downloadImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.download
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(downloadImage)
        .bigLargeIcon(null)


    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.LoadApp_notification_channel_id)
    )
        .setSmallIcon(R.drawable.download)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(filename)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setStyle(bigPicStyle)
        .setLargeIcon(downloadImage)
        .addAction(
            R.drawable.ic_clouddown,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
