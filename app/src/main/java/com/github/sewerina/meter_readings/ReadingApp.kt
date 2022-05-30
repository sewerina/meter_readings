package com.github.sewerina.meter_readings

import android.app.Application
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.di.DaggerMainComponent
import com.github.sewerina.meter_readings.di.AppContextModule
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.PeriodicWorkRequest
import com.github.sewerina.meter_readings.notification.NotificationWorker
import androidx.work.WorkManager
import androidx.work.WorkInfo
import androidx.work.ExistingPeriodicWorkPolicy
import com.github.sewerina.meter_readings.database.AppDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.di.MainComponent
import java.util.concurrent.TimeUnit

class ReadingApp : Application() {
    private var mOnSharedPreferenceChangeListener: OnSharedPreferenceChangeListener? = null
    override fun onCreate() {
        super.onCreate()
        sMainComponent =
            DaggerMainComponent.builder().appContextModule(AppContextModule(this)).build()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        mOnSharedPreferenceChangeListener =
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
        preferences.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener)
//        val db = Room.databaseBuilder(
//            this,
//            AppDatabase::class.java,
//            "reading.db"
//        ) //                .allowMainThreadQueries()
//            .addCallback(object : RoomDatabase.Callback() {
//                override fun onCreate(db: SupportSQLiteDatabase) {
//                    super.onCreate(db)
//                    db.execSQL("INSERT INTO home (address) VALUES('Мой дом');")
//                }
//            })
//            .build()
//        sReadingDao = db.readingDao()
    }

    companion object {
        private const val TAG = "ReadingApp"
        var sReadingDao: AppDao? = null
        @JvmField
        var sMainComponent: MainComponent? = null
    }
}