package com.github.sewerina.meter_readings.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AppDao {

    @Query("delete from reading")
    Completable clearTableReadingRx();

    @Query("delete from home")
    Completable clearTableHomeRx();

    @Insert()
    Completable insertInTableReadingRx(List<ReadingEntity> readingEntities);

    @Insert()
    Completable insertInTableHomeRx(List<HomeEntity> homeEntities);

    @Query("select * from reading")
    List<ReadingEntity> getAllReadings();

    @Query("select * from reading")
    Single<List<ReadingEntity>> getAllReadingsRx();

    @Query("select * from reading where homeId =:homeId order by date desc")
    Single<List<ReadingEntity>> getReadingsForHomeRx(int homeId);

    @Query("select * from home")
    Single<List<HomeEntity>> getHomesRx();

    @Query("select * from home")
    LiveData<List<HomeEntity>> getHomesLiveData();

//    @Insert
//    Completable insertReadingRx(ReadingEntity entity);

    @Insert
    Single<Long> insertReadingRx(ReadingEntity entity);

    @Update
    Completable updateReadingRx(ReadingEntity entity);

    @Delete
    Completable deleteReadingRx(ReadingEntity entity);

//    @Insert
//    Completable insertHomeRx(HomeEntity entity);

    @Insert
    Single<Long> insertHomeRx(HomeEntity entity);

    @Update
    Completable updateHomeRx(HomeEntity entity);

//    @Update
//    Single<Long> updateHomeRx(HomeEntity entity);

    @Delete
    Completable deleteHomeRx(HomeEntity entity);

}
