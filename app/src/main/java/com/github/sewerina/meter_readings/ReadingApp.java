package com.github.sewerina.meter_readings;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.AppDatabase;
import com.github.sewerina.meter_readings.notification.NotificationWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class ReadingApp extends Application {

    private static final String TAG = "ReadingApp";
    public static AppDao mReadingDao;
    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("notification")) {
                    boolean hasNotification = sharedPreferences.getBoolean("notification", true);

                    if (hasNotification) {
                        PeriodicWorkRequest notificationWorkRequest = new PeriodicWorkRequest
                                .Builder(NotificationWorker.class, 1, TimeUnit.HOURS)
                                .addTag("notification")
                                .build();

                        WorkManager.getInstance(ReadingApp.this)
                                .getWorkInfosForUniqueWorkLiveData("notification")
                                .observeForever(new Observer<List<WorkInfo>>() {
                                    @Override
                                    public void onChanged(List<WorkInfo> workInfos) {
                                        for (WorkInfo workInfo : workInfos) {
                                            Log.i(TAG, "onChanged: " + workInfo.getState().name());
                                        }

                                    }
                                });

                        WorkManager.getInstance(ReadingApp.this).enqueueUniquePeriodicWork(
                                "notification",
                                ExistingPeriodicWorkPolicy.REPLACE,
                                notificationWorkRequest);
                        Log.i(TAG, "onSharedPreferenceChanged: включили нотификацию " + "запуск worker");
                    } else {
                        WorkManager.getInstance(ReadingApp.this).cancelAllWorkByTag("notification");
                        Log.i(TAG, "onSharedPreferenceChanged: отключили нотификацию " + "отмена worker");
                    }
                }


            }
        };
        preferences.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

        AppDatabase db = Room.databaseBuilder(
                this,
                AppDatabase.class,
                "reading.db")
                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        db.execSQL("INSERT INTO home (address) VALUES('Мой дом');");
                    }
                })
                .build();

        mReadingDao = db.readingDao();
    }
}
