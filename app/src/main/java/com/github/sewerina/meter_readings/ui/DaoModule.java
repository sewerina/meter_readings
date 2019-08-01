package com.github.sewerina.meter_readings.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = AppContextModule.class)
public class DaoModule {

    @Provides
    @Singleton
    public AppDao providesDao(AppDatabase appDatabase) {
        return appDatabase.readingDao();
    }

    @Provides
    @Singleton
    public AppDatabase providesDatabase(Application appContext) {
        return Room.databaseBuilder(
                appContext,
                AppDatabase.class,
                "reading.db")
//                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        db.execSQL("INSERT INTO home (address) VALUES('Мой дом');");
                    }
                })
                .build();
    }

}
