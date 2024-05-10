package com.projecte.mewnagochi.services.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.projecte.mewnagochi.MainActivity
import com.projecte.mewnagochi.R


class MyFirebaseMessagingService : FirebaseMessagingService() {

    lateinit var firebaseMessageToken: String
    fun checkToken() {
        if (!this::firebaseMessageToken.isInitialized) {
            obtainFCMToken()
        }
    }

    fun obtainFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isComplete) {
                Log.i("FCM_token", "getInstanceId failed", it.exception)
                firebaseMessageToken = ""
                return@addOnCompleteListener
            }

            firebaseMessageToken = it.result.toString()
            Log.i("FCM_token", "FCM Token: $firebaseMessageToken")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("Notification", "New token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // Implement this method to send token to your app server
        Log.i("Notification", "Token sent to server: $token)")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Recieved", "Message data payload: ${remoteMessage.data}")

            // Check if data needs to be processed by long running job
//            if (needsToBeScheduled()) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
        }

        displayNotification(applicationContext, remoteMessage.notification?.title, remoteMessage.notification?.body)
    }

    fun displayNotification(context: Context, title: String?, body: String?) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.default_notification_channel_id)
        Log.i("Recieved", "Channel ID: $channelId")
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.window)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}

