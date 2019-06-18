package com.github.sewerina.meter_readings;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public long fromDate(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public Date fromTime(long time) {
        return new Date(time);
    }

}
