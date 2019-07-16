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

    @Query("select * from reading where homeId =:homeId order by date desc")
    List<ReadingEntity> getReadingsForHome(int homeId);

    @Query("select * from reading where homeId =:homeId order by date desc")
    Single<List<ReadingEntity>> getReadingsForHomeRx(int homeId);

    @Query("select * from home")
    List<HomeEntity> getHomes();

    @Query("select * from home")
    LiveData<List<HomeEntity>> getHomesLiveData();

    @Insert
    Completable insertReadingRx(ReadingEntity entity);

    @Update
    Completable updateReadingRx(ReadingEntity entity);

    @Delete
    Completable deleteReadingRx(ReadingEntity entity);

    @Insert
    Completable insertHomeRx(HomeEntity entity);

    @Update
    Completable updateHomeRx(HomeEntity entity);

    @Delete
    Completable deleteHomeRx(HomeEntity entity);

}
