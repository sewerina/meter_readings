package com.github.sewerina.meter_readings.notification

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val applicationContext = applicationContext
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formatMinutes = SimpleDateFormat("mm", Locale.getDefault())
        val formatHours = SimpleDateFormat("HH", Locale.getDefault())
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val strDay = preferences.getString("day", "15")
        val day = strDay!!.toInt()
        val startTime = preferences.getString("start time", "18:00")
        val endTime = preferences.getString("end time", "20:00")
        val start: Date
        val end: Date
        val startSumMin: Int
        val endSumMin: Int
        try {
            start = sdf.parse(startTime)
            val startMinutes = formatMinutes.format(start)
            val startHours = formatHours.format(start)
            startSumMin = startHours.toInt() * 60 + startMinutes.toInt()
            end = sdf.parse(endTime)
            val endMinutes = formatMinutes.format(end)
            val endHours = formatHours.format(end)
            endSumMin = endHours.toInt() * 60 + endMinutes.toInt()
        } catch (e: ParseException) {
            e.printStackTrace()
            return Result.failure()
        }
        val cal = Calendar.getInstance()
        val currentDayOfMonth = cal[Calendar.DAY_OF_MONTH]
        val currentHourOfDay = cal[Calendar.HOUR_OF_DAY]
        val currentMinOfHour = cal[Calendar.MINUTE]
        val currentSumMin = currentHourOfDay * 60 + currentMinOfHour
        return try {
            if (day != currentDayOfMonth) {
                Log.i(TAG, "doWork: нет совпадения по дате")
                return Result.success()
            }
            if (currentSumMin < startSumMin || currentSumMin > endSumMin) {
                Log.i(TAG, "doWork: нет совпадения по времени")
                return Result.success()
            }
            RemindNotification(applicationContext).appearNotification()
            Log.i(TAG, "doWork: success")
            Result.success()
        } catch (throwable: Throwable) {
            Log.i(TAG, "doWork: throwable - $throwable")
            throwable.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private val TAG = NotificationWorker::class.java.simpleName
    }
}