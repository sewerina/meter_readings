package com.github.sewerina.meter_readings.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.ui.readings_main.MainActivity

class RemindNotification(private val mContext: Context) {
    fun appearNotification() {
        val notificationManager = NotificationManagerCompat.from(mContext)
        // notificationId is a unique int for each notification that you must define
        val notificationId = 111
        notificationManager.notify(notificationId, createNotification(mContext))
    }

    private fun createNotification(context: Context): Notification {
        val textContent = "Не забудьте записать Ваши показания и передать их в Управляющую компанию"
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_notification)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(textContent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(textContent)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent(context))
            .setAutoCancel(true)
        return builder.build()
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.channel_name)
            val description = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun contentIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, intent, 0)
        }
    }

    companion object {
        private const val CHANNEL_ID = "ReadingNotification"
    }

    init {
        createNotificationChannel(mContext)
    }
}