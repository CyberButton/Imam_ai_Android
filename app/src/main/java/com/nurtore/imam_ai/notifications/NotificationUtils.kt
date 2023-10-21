package com.nurtore.imam_ai.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.nurtore.imam_ai.R

//private fun createNotificationChannel(context: Context) {
//    // Create the NotificationChannel, but only on API 26+ because
//    // the NotificationChannel class is not in the Support Library.
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val name = "Prayer Reminder"
//        val descriptionText = "Schduled prayer times reminder"
//        val importance = NotificationManager.IMPORTANCE_HIGH
//        val CHANNEL_ID = "prayerNotification"
//        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//            description = descriptionText
//        }
//        // Register the channel with the system.
//        val notificationManager: NotificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }
//}

fun showPrayerNotification(context: Context, prayerName: String) {
    val channelId = "prayer_notifications_channel"
    val notificationId = prayerName.hashCode()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Prayer Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Prayer Time Reminder")
        .setContentText("$prayerName prayer time is now.")
        .setSmallIcon(R.drawable.imam) // Set your own icon here
//        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()


    notificationManager.notify(notificationId, notification)
}
