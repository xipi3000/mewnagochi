package com.projecte.mewnagochi.services.notification

import android.content.Intent
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.projecte.mewnagochi.services.auth.AccountServiceImpl

class MyFirebaseMessagingService: FirebaseMessagingService() {
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
                Log.i("FCM_Token", "getInstanceId failed", it.exception)
                firebaseMessageToken = ""
                return@addOnCompleteListener
            }
            firebaseMessageToken = it.result.toString()
            Log.i("FCM_Token", "FCM Token: $firebaseMessageToken")
            sendRegistrationToServer(firebaseMessageToken)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("FCM_Token", "New token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
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
        // When recieving message, relay notification display to background service
        // This way, notifications are displayed even with the application killed
        val intent = Intent(applicationContext, myBackgroundService::class.java)
        intent.putExtra("title", remoteMessage.notification!!.title)
        intent.putExtra("body", remoteMessage.notification?.body ?: "")
        startService(intent)
    }

    fun modifyHourValue(newHour: Int) {
        database.getReference("FCMTokens").child(firebaseMessageToken)
            .child("notification").setValue(newHour)
    }
}