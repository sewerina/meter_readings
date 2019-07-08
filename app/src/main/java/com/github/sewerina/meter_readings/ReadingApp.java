package com.github.sewerina.meter_readings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class ReadingApp extends Application {

    public static AppDao mReadingDao;

    @Override
    public void onCreate() {
        super.onCreate();

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
