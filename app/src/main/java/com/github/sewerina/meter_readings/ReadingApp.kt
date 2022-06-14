package com.github.sewerina.meter_readings

import android.app.Application
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.github.sewerina.meter_readings.notification.NotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class ReadingApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val onSharedPreferenceChangeListener =
            OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == "notification") {
                    val hasNotification = sharedPreferences.getBoolean("notification", true)
                    if (hasNotification) {
                        val notificationWorkRequest = PeriodicWorkRequest.Builder(
                            NotificationWorker::class.java, 1, TimeUnit.HOURS
                        )
                            .addTag("notification")
                            .build()
                        WorkManager.getInstance(this@ReadingApp)
                            .getWorkInfosForUniqueWorkLiveData("notification")
                            .observeForever { workInfos ->
                                for (workInfo in workInfos) {
                                    Log.i(TAG, "WorkManager workInfo: " + workInfo.state.name)
                                }
                            }
                        WorkManager.getInstance(this@ReadingApp).enqueueUniquePeriodicWork(
                            "notification",
                            ExistingPeriodicWorkPolicy.REPLACE,
                            notificationWorkRequest
                        )
                        Log.i(
                            TAG,
                            "onSharedPreferenceChanged: включили нотификацию " + "запуск worker"
                        )
                    } else {
                        WorkManager.getInstance(this@ReadingApp).cancelAllWorkByTag("notification")
                        Log.i(
                            TAG,
                            "onSharedPreferenceChanged: отключили нотификацию " + "отмена worker"
                        )
                    }
                }
            }
        preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    companion object {
        private const val TAG = "ReadingApp"
    }
}