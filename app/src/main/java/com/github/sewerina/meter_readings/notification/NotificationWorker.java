package com.github.sewerina.meter_readings.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationWorker extends Worker {
    private static final String TAG = NotificationWorker.class.getSimpleName();

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat formatMinutes = new SimpleDateFormat("mm", Locale.getDefault());
        SimpleDateFormat formatHours = new SimpleDateFormat("HH", Locale.getDefault());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String strDay = preferences.getString("day", "15");
        int day = Integer.parseInt(strDay);
        String startTime = preferences.getString("start time", "18:00");
        String endTime = preferences.getString("end time", "20:00");

        Date start;
        Date end;
        int startSumMin;
        int endSumMin;
        try {
            start = sdf.parse(startTime);
            String startMinutes = formatMinutes.format(start);
            String startHours = formatHours.format(start);
            startSumMin = Integer.parseInt(startHours) * 60 + Integer.parseInt(startMinutes);

            end = sdf.parse(endTime);
            String endMinutes = formatMinutes.format(end);
            String endHours = formatHours.format(end);
            endSumMin = Integer.parseInt(endHours) * 60 + Integer.parseInt(endMinutes);
        } catch (ParseException e) {
            e.printStackTrace();
            return Result.failure();
        }

        Calendar cal = Calendar.getInstance();
        int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int currentHourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinOfHour = cal.get(Calendar.MINUTE);
        int currentSumMin = currentHourOfDay * 60 + currentMinOfHour;

        try {
            if (day != currentDayOfMonth) {
                Log.i(TAG, "doWork: нет совпадения по дате");
                return Result.success();
            }
            if (currentSumMin < startSumMin || currentSumMin > endSumMin) {
                Log.i(TAG, "doWork: нет совпадения по времени");
                return Result.success();
            }

            new RemindNotification(applicationContext).appearNotification();
            Log.i(TAG, "doWork: success");
            return Result.success();

        } catch (Throwable throwable) {
            Log.i(TAG, "doWork: throwable - " + throwable);
            return Result.retry();
        }

    }
}
