package com.projecte.mewnagochi.services.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.projecte.mewnagochi.MainActivity
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.services.auth.AccountServiceImpl


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var firebaseMessageToken: String
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://mewnagochi-default-rtdb.europe-west1.firebasedatabase.app")
    private val auth = AccountServiceImpl()
    private lateinit var userID: String
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
            sendRegistrationToServer(firebaseMessageToken)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("Notification", "New token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // Database schema: FCMTokens/Token/Hour_to_get_notified
        // Try to get the notification hour value
            // It works -> token already stored, don't modify it's configured hour
            // It fails -> add new token with default hour

        val ref = database.getReference("FCMTokens").child(token)
        ref.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val value = it.result?.value
                if (value == null) {
                    userID = auth.getUserId()
                    Log.i("FCM", "Setting default value")
                    ref.child("notification").setValue(20)
                    ref.child("associatedTo").setValue(userID)
                } else{
                    Log.i("FCM", "Configured values found, not modifying")
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        displayNotification(applicationContext, remoteMessage.data["title"], remoteMessage.data["body"])
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

    fun modifyHourValue(newHour: Int) {
        database.getReference("FCMTokens").child(firebaseMessageToken)
            .child("notification").setValue(newHour)
    }

}