package com.github.sewerina.meter_readings.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ReadingEntity.class, HomeEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao readingDao();

}
