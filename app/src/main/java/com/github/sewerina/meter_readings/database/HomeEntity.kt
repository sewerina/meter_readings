package com.github.sewerina.meter_readings.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "home")
class HomeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @JvmField
    var id : Int,
    @ColumnInfo(name = "address")
    @JvmField
    var address: String
): Serializable

class NewHomeEntity(
    @ColumnInfo(name = "address")
    @JvmField
    var address: String = ""
): Serializable