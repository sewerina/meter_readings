package com.github.sewerina.meter_readings.database

import androidx.room.*
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "reading",
    foreignKeys = [ForeignKey(
        entity = HomeEntity::class,
        parentColumns = ["id"],
        childColumns = ["homeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("homeId")]
)
class ReadingEntity(
    @JvmField
    @ColumnInfo(name = "homeId")
    var homeId: Int,

    @JvmField
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @JvmField
    @ColumnInfo(name = "date")
    @TypeConverters(DateConverter::class)
    var date: Date,

    @JvmField
    @ColumnInfo(name = "coldWater")
    var coldWater: Int,

    @JvmField
    @ColumnInfo(name = "hotWater")
    var hotWater: Int,

    @JvmField
    @ColumnInfo(name = "drainWater")
    var drainWater: Int,

    @JvmField
    @ColumnInfo(name = "electricity")
    var electricity: Int,

    @JvmField
    @ColumnInfo(name = "gas")
    var gas: Int
) : Serializable

class NewReadingEntity(
    @JvmField
    @ColumnInfo(name = "date")
    @TypeConverters(DateConverter::class)
    var date: Date,

    @JvmField
    @ColumnInfo(name = "homeId")
    var homeId: Int = -1,

    @JvmField
    @ColumnInfo(name = "coldWater")
    var coldWater: Int = 0,

    @JvmField
    @ColumnInfo(name = "hotWater")
    var hotWater: Int = 0,

    @JvmField
    @ColumnInfo(name = "drainWater")
    var drainWater: Int = 0,

    @JvmField
    @ColumnInfo(name = "electricity")
    var electricity: Int = 0,

    @JvmField
    @ColumnInfo(name = "gas")
    var gas: Int = 0
) : Serializable {
    fun toEntity(id: Int): ReadingEntity {
        return ReadingEntity(homeId, id, date, coldWater, hotWater, drainWater, electricity, gas)
    }
}