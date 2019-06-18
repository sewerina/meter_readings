package com.github.sewerina.meter_readings;

import android.app.Application;

import androidx.room.Room;

public class ReadingApp extends Application {

    public static ReadingDao mReadingDao;

    @Override
    public void onCreate() {
        super.onCreate();

        AppDatabase db = Room.databaseBuilder(
                this,
                AppDatabase.class,
                "reading.db")
                .allowMainThreadQueries()
                .build();

        mReadingDao = db.readingDao();
    }
}
