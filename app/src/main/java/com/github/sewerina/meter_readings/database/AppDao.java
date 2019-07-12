package com.github.sewerina.meter_readings.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppDao {

    @Query("select * from reading order by date desc")
    List<ReadingEntity> getAll();

    @Query("select * from reading where homeId =:homeId order by date desc")
    List<ReadingEntity> getReadingsForHome(int homeId);

    @Query("select * from home")
    List<HomeEntity> getHomes();

    @Insert
    void insertReading(ReadingEntity entity);

    @Update
    void updateReading(ReadingEntity entity);

    @Delete
    void deleteReading(ReadingEntity entity);

    @Insert
    void insertHome(HomeEntity entity);

    @Update
    void updateHome(HomeEntity entity);

    @Delete
    void deleteHome(HomeEntity entity);

}
