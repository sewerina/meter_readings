package com.github.sewerina.meter_readings.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.AppDatabase
import com.github.sewerina.meter_readings.ui.MessageService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesDao(appDatabase: AppDatabase): AppDao {
        return appDatabase.readingDao()
    }

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext appContext: Context): AppDatabase {
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

    @Provides
    @Singleton
    fun firebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    @Named("readings")
    fun referenceReadings(firebaseFirestore: FirebaseFirestore): CollectionReference {
        return firebaseFirestore.collection("readings")
    }

    @Provides
    @Singleton
    @Named("homes")
    fun referenceHomes(firebaseFirestore: FirebaseFirestore): CollectionReference {
        return firebaseFirestore.collection("homes")
    }

    @Provides
    @Singleton
    @Named("reports")
    fun referenceReports(firebaseFirestore: FirebaseFirestore): CollectionReference {
        return firebaseFirestore.collection("reports")
    }

    @Provides
    fun messageService(@ApplicationContext appContext: Context): MessageService {
        return MessageService(appContext)
    }
}