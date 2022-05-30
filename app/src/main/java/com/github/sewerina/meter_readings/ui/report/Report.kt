package com.github.sewerina.meter_readings.ui.report

import com.github.sewerina.meter_readings.FormattedDate
import com.github.sewerina.meter_readings.database.ReadingEntity
import java.io.Serializable
import java.util.*

class Report : Serializable {
    var coldWaterValue = 0

    var hotWaterValue = 0

    var drainWaterValue = 0

    var electricityValue = 0

    var gasValue = 0

    var date: Date? = null

    var homeId = 0

    constructor(readings: List<ReadingEntity>) {
        getValues(readings)
    }

    constructor() {}

    private fun getValues(readings: List<ReadingEntity>) {
        val firstReading = readings[readings.size - 1]
        val lastReading = readings[0]
        homeId = firstReading.homeId
        date = lastReading.date
        coldWaterValue = lastReading.coldWater - firstReading.coldWater
        hotWaterValue = lastReading.hotWater - firstReading.hotWater
        drainWaterValue = lastReading.drainWater - firstReading.drainWater
        electricityValue = lastReading.electricity - firstReading.electricity
        gasValue = lastReading.gas - firstReading.gas
    }

    fun reportMessage(
        isColdWater: Boolean,
        isHotWater: Boolean,
        isDrainWater: Boolean,
        isElectricity: Boolean,
        isGas: Boolean,
        home: String
    ): String {
//        String lastDate = new FormattedDate(mLastDate).text();
        val stringDate = date?.let { FormattedDate(it).text() }
        val coldWater = " хол.вода = $coldWaterValue"
        val hotWater = " гор.вода = $hotWaterValue"
        val drainWater = " канализация = $drainWaterValue"
        val electricity = " электричество = $electricityValue"
        val gas = " gas = $gasValue"
        var message = ("Расходы компонентов для дома " + home + " на дату " + stringDate
                + " составляют:")
        if (isColdWater) {
            message += coldWater
        }
        if (isHotWater) {
            message += hotWater
        }
        if (isDrainWater) {
            message += drainWater
        }
        if (isElectricity) {
            message += electricity
        }
        if (isGas) {
            message += gas
        }
        return message
    }
}