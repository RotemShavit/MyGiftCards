package com.postpc.mygiftcrads

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class NotificationClass(private val context: Context) {

    val DATE_CHANNEL_ID = "DATE"

    fun fireDateNotification(msg: String)
    {
        createDateChannelIfNotExists()
        createNotification(msg)
//        createDelayedNotification(msg, delay)
    }

    private fun createDateChannelIfNotExists()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val DATE_CHANNEL_NAME = "Date notifications"
            val DATE_CHANNEL_DESCRIPTION = "Get notification by gift cards deadlines"
            val DATE_IMPORTANTCE = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(DATE_CHANNEL_ID, DATE_CHANNEL_NAME, DATE_IMPORTANTCE)
            channel.description = DATE_CHANNEL_DESCRIPTION
            channel.enableLights(true)
            channel.lightColor = Color.GREEN

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    private fun createNotification(msg: String)
    {
        val intent = Intent(context, LoadingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        val pendingIntent = PendingIntent.getActivity(context, 1, intent, 0)

        val notification = NotificationCompat.Builder(context, DATE_CHANNEL_ID)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.notification_logo_foreground)
            .setColor(ContextCompat.getColor(context, R.color.notification_color))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1, notification)
    }

    fun createDelayedNotification(msg: String, delay: Long)
    {

    }
}