package com.github.sewerina.meter_readings.ui.report;

import com.github.sewerina.meter_readings.FormattedDate;
import com.github.sewerina.meter_readings.database.ReadingEntity;

import java.util.Date;
import java.util.List;

public class Report {

    private int coldWaterValue;
    private int hotWaterValue;
    private int drainWaterValue;
    private int electricityValue;
    private int gasValue;

    private Date mLastDate;

    private int homeId;

    public Report(List<ReadingEntity> readings) {
        getValues(readings);
    }

    private void getValues(List<ReadingEntity> readings) {
        ReadingEntity firstReading = readings.get(readings.size() - 1);
        ReadingEntity lastReading = readings.get(0);
        homeId = firstReading.homeId;
        mLastDate = lastReading.date;
        coldWaterValue = lastReading.coldWater - firstReading.coldWater;
        hotWaterValue = lastReading.hotWater - firstReading.hotWater;
        drainWaterValue = lastReading.drainWater - firstReading.drainWater;
        electricityValue = lastReading.electricity - firstReading.electricity;
        gasValue = lastReading.gas - firstReading.gas;
    }

    public int getHomeId() {
        return homeId;
    }

    public int getColdWaterValue() {
        return coldWaterValue;
    }

    public int getHotWaterValue() {
        return hotWaterValue;
    }

    public int getDrainWaterValue() {
        return drainWaterValue;
    }

    public int getElectricityValue() {
        return electricityValue;
    }

    public int getGasValue() {
        return gasValue;
    }

    public String reportMessage(boolean isColdWater, boolean isHotWater, boolean isDrainWater, boolean isElectricity, boolean isGas, String home) {
        String lastDate = new FormattedDate(mLastDate).text();

        String coldWater = " хол.вода = " + getColdWaterValue();
        String hotWater = " гор.вода = " + getHotWaterValue();
        String drainWater = " канализация = " + getDrainWaterValue();
        String electricity = " электричество = " + getElectricityValue();
        String gas = " gas = " + getGasValue();

        String message = "Расходы компонентов для дома " + home + " на дату " + lastDate
                + " составляют:";

        if (isColdWater) {
            message = message + coldWater;
        }
        if (isHotWater) {
            message = message + hotWater;
        }
        if (isDrainWater) {
            message = message + drainWater;
        }
        if (isElectricity) {
            message = message + electricity;
        }
        if (isGas) {
            message = message + gas;
        }

        return message;
    }

}
