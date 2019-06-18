package com.github.sewerina.meter_readings;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReadingDao {

    @Query("select * from reading")
    List<ReadingEntity> getAll();

    @Insert
    void insert(ReadingEntity entity);

    @Update
    void update(ReadingEntity entity);

    @Delete
    void delete(ReadingEntity entity);

}
