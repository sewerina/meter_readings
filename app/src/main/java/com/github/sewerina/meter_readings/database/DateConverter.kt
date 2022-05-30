package com.github.sewerina.meter_readings.database

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun fromTime(time: Long): Date {
        return Date(time)
    }
}