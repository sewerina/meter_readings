package com.github.sewerina.meter_readings;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reading")
class ReadingEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    public int id;

    @ColumnInfo(name="date")
    public String date;

    @ColumnInfo(name="coldWater")
    public int coldWater;

    @ColumnInfo(name="hotWater")
    public int hotWater;

    @ColumnInfo(name="drainWater")
    public int drainWater;

    @ColumnInfo(name="electricity")
    public int electricity;

    @ColumnInfo(name="gas")
    public int gas;

    public ReadingEntity() {

    }

}
