package com.projecte.mewnagochi.services.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.projecte.mewnagochi.services.notification.NotificationUtils.displayNotification


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a notification payload
        if (remoteMessage.notification != null) {
            // Handle notification message
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body

            // Display notification
            displayNotification(
                applicationContext, title, body
            )
        }
    }
}

object NotificationUtils {
    fun displayNotification(context: Context, title: String?, body: String?) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "my_channel_id"
        val channelName = "My Channel"

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)

        notificationManager.createNotificationChannel(channel)

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId).setContentTitle(title)
                .setContentText(body).setAutoCancel(true)

        notificationManager.notify(0, builder.build())
    }
}