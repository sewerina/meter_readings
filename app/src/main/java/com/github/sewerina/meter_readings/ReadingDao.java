package com.github.sewerina.meter_readings;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReadingDao {

    @Query("select * from reading order by date desc")
    List<ReadingEntity> getAll();

    @Query("select * from reading where homeId =:homeId order by date desc")
    List<ReadingEntity> getReadingsForHome(int homeId);

    @Query("select * from home")
    List<HomeEntity> getHomes();

    @Insert
    void insert(ReadingEntity entity);

    @Update
    void update(ReadingEntity entity);

    @Delete
    void delete(ReadingEntity entity);

    @Insert
    void createHome(HomeEntity entity);

}
