package com.projecte.mewnagochi.services.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.projecte.mewnagochi.MainActivity
import com.projecte.mewnagochi.R

class myBackgroundService: Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // Get notification data and display
        val title = intent.getStringExtra("title")
        val body = intent.getStringExtra("body")
        displayNotification(applicationContext, title, body)
        // When done, stop service so it doesn't consume resources
        stopSelf()
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun displayNotification(context: Context, title: String?, body: String?) {
        Log.i("message", "Displaying notification from my fun")
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.window)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }
}