package com.github.sewerina.meter_readings.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AppDao {
    @Query("delete from reading")
    suspend fun clearTableReading()

    @Query("delete from home")
    suspend fun clearTableHome()

    @Insert
    suspend fun insertInTableReading(readingEntities: List<ReadingEntity>)

    @Insert
    suspend fun insertInTableHome(homeEntities: List<HomeEntity>)

    @Query("select * from reading")
    suspend fun allReadings(): List<ReadingEntity>

    @Query("select * from reading where homeId =:homeId order by date desc")
    suspend fun getReadingsForHome(homeId: Int): List<ReadingEntity>

    @Query("select * from home")
    suspend fun homes(): List<HomeEntity>

    @get:Query("select * from home")
    val homesLiveData: LiveData<List<HomeEntity>>

    @Insert(entity = ReadingEntity::class)
    suspend fun insertReading(entity: NewReadingEntity): Long

    @Update
    suspend fun updateReading(entity: ReadingEntity)

    @Delete
    suspend fun deleteReading(entity: ReadingEntity)

    @Insert(entity = HomeEntity::class)
    suspend fun insertHome(entity: NewHomeEntity): Long

    @Update
    suspend fun updateHome(entity: HomeEntity)

    @Delete
    suspend fun deleteHome(entity: HomeEntity)
}