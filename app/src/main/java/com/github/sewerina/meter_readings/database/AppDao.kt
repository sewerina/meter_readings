package com.github.sewerina.meter_readings.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface AppDao {
    @Query("delete from reading")
    fun clearTableReadingRx(): Completable

    @Query("delete from home")
    fun clearTableHomeRx(): Completable

    @Insert
    fun insertInTableReadingRx(readingEntities: List<ReadingEntity>): Completable

    @Insert
    fun insertInTableHomeRx(homeEntities: List<HomeEntity>): Completable

    @get:Query("select * from reading")
    val allReadings: List<ReadingEntity>

    @get:Query("select * from reading")
    val allReadingsRx: Single<List<ReadingEntity>>

    @Query("select * from reading where homeId =:homeId order by date desc")
    fun getReadingsForHomeRx(homeId: Int): Single<List<ReadingEntity>>

    @get:Query("select * from home")
    val homesRx: Single<List<HomeEntity>>

    @get:Query("select * from home")
    val homesLiveData: LiveData<List<HomeEntity>>

    @Insert(entity = ReadingEntity::class)
    fun insertReadingRx(entity: NewReadingEntity): Single<Long>

    @Update
    fun updateReadingRx(entity: ReadingEntity): Completable

    @Delete
    fun deleteReadingRx(entity: ReadingEntity): Completable

    @Insert(entity = HomeEntity::class)
    fun insertHomeRx(entity: NewHomeEntity): Single<Long>

    @Update
    fun updateHomeRx(entity: HomeEntity): Completable

    @Delete
    fun deleteHomeRx(entity: HomeEntity): Completable
}