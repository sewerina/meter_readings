package com.github.sewerina.meter_readings.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.sewerina.meter_readings.R;
import com.github.sewerina.meter_readings.ui.readings_main.MainActivity;

public class RemindNotification {

    private final static String CHANNEL_ID = "ReadingNotification";
    private Context mContext;

    public RemindNotification(Context context) {
        mContext = context;
        createNotificationChannel(mContext);
    }

    public void appearNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
// notificationId is a unique int for each notification that you must define
        int notificationId = 111;
        notificationManager.notify(notificationId, createNotification(mContext));
    }

    private Notification createNotification(Context context) {
        String textContent = "Не забудьте записать Ваши показания и передать их в Управляющую компанию";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(textContent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(textContent))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

                return builder.build();
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private PendingIntent contentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }



}
