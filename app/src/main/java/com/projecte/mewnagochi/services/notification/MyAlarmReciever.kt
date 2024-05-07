package com.projecte.mewnagochi.services.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.util.Calendar


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Check if it's time to send the notification
        val notificationHour = getUserNotificationHour(context)
        val currentHour = currentHour

        if (currentHour == notificationHour) {
            // Send the notification
            sendNotification(context)
        }
    }

    private fun sendNotification(applicationContext: Context) {
        // Lògica de la notificació:
        // - Obtenir dades average de l'usuari
        // - Comparar amb dades actuals
        // - Montar notificació segons diferència

        // Construct notification payload
        val builder = RemoteMessage.Builder("your_fcm_token@gcm.googleapis.com")
        builder.setMessageId((System.currentTimeMillis() / 1000).toInt().toString())
        builder.addData("title", "Your Notification Title")
        builder.addData("body", "Your Notification Body")
        val message = builder.build()

        // Send notification
        FirebaseMessaging.getInstance().send(message)
    }

    private fun getUserNotificationHour(context: Context): Int {
        // TODO: implement logic to retrieve this value, such as from SharedPreferences
        return 19 // Default to 23 (11 PM) for now
    }

    private val currentHour: Int
        get() {
            // Get the current hour
            val calendar: Calendar = Calendar.getInstance()
            return calendar.get(Calendar.HOUR_OF_DAY)
        }
}
