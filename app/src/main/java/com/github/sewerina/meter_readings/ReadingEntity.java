package com.github.sewerina.meter_readings;

class ReadingEntity {

    String mDate;
    int mColdWater;
    int mHotWater;
    int mDrainWater;
    int mElectricity;
    int mGas;


    public ReadingEntity(String date, int coldWater, int hotWater, int drainWater, int electricity, int gas) {
        mDate = date;
        mColdWater = coldWater;
        mHotWater = hotWater;
        mDrainWater = drainWater;
        mElectricity = electricity;
        mGas = gas;
    }
}
