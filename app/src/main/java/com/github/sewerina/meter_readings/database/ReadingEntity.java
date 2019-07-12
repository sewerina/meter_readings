package com.github.sewerina.meter_readings.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "reading",
        foreignKeys = @ForeignKey(entity = HomeEntity.class,
                parentColumns = "id",
                childColumns = "homeId"), indices = {@Index("homeId")})
public class ReadingEntity implements Serializable {
    @ColumnInfo(name = "homeId")
    public int homeId;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "date")
    @TypeConverters({DateConverter.class})
    public Date date;

    @ColumnInfo(name = "coldWater")
    public int coldWater;

    @ColumnInfo(name = "hotWater")
    public int hotWater;

    @ColumnInfo(name = "drainWater")
    public int drainWater;

    @ColumnInfo(name = "electricity")
    public int electricity;

    @ColumnInfo(name = "gas")
    public int gas;

    public ReadingEntity() {

    }

}
