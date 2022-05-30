package com.github.sewerina.meter_readings.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [AppContextModule::class])
class DaoModule {
    @Provides
    @Singleton
    fun providesDao(appDatabase: AppDatabase): AppDao {
        return appDatabase.readingDao()
    }

    @Provides
    @Singleton
    fun providesDatabase(appContext: Application): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "reading.db"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL("INSERT INTO home (address) VALUES('Мой дом');")
                }
            })
            .build()
    }
}