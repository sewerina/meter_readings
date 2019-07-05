package com.github.sewerina.meter_readings;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;

import androidx.preference.PreferenceManager;

public class ReadingPreferences {
    public static final String KEY_PREF_COLD_WATER = "coldWater";
    public static final String KEY_PREF_HOT_WATER = "hotWater";
    public static final String KEY_PREF_DRAIN_WATER = "drainWater";
    public static final String KEY_PREF_ELECTRICITY = "electricity";
    public static final String KEY_PREF_GAS = "gas";

    private SharedPreferences mSharedPreferences;

    public ReadingPreferences(Context context) {
        if (context != null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public void setLayoutVisibility(View view) {
        LinearLayout coldWaterLl = view.findViewById(R.id.ll_coldWater);
        LinearLayout hotWaterLl = view.findViewById(R.id.ll_hotWater);
        LinearLayout drainWaterLl = view.findViewById(R.id.ll_drainWater);
        LinearLayout electricityLl = view.findViewById(R.id.ll_electricity);
        LinearLayout gasLl = view.findViewById(R.id.ll_gas);

        boolean isColdWater = mSharedPreferences.getBoolean(KEY_PREF_COLD_WATER, true);
        boolean isHotWater = mSharedPreferences.getBoolean(KEY_PREF_HOT_WATER, true);
        boolean isDrainWater = mSharedPreferences.getBoolean(KEY_PREF_DRAIN_WATER, true);
        boolean isElectricity = mSharedPreferences.getBoolean(KEY_PREF_ELECTRICITY, true);
        boolean isGas = mSharedPreferences.getBoolean(KEY_PREF_GAS, true);

        coldWaterLl.setVisibility(isColdWater ? View.VISIBLE : View.GONE);
        hotWaterLl.setVisibility(isHotWater ? View.VISIBLE : View.GONE);
        drainWaterLl.setVisibility(isDrainWater ? View.VISIBLE : View.GONE);
        electricityLl.setVisibility(isElectricity ? View.VISIBLE : View.GONE);
        gasLl.setVisibility(isGas ? View.VISIBLE : View.GONE);
    }
}
