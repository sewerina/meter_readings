package com.github.sewerina.meter_readings.ui.readings_main

import android.view.View
import android.widget.LinearLayout
import androidx.preference.PreferenceManager
import com.github.sewerina.meter_readings.R

class ReadingPreferences() {
    companion object {
        const val KEY_PREF_COLD_WATER = "coldWater"
        const val KEY_PREF_HOT_WATER = "hotWater"
        const val KEY_PREF_DRAIN_WATER = "drainWater"
        const val KEY_PREF_ELECTRICITY = "electricity"
        const val KEY_PREF_GAS = "gas"
    }

    fun setLayoutVisibility(view: View) {
        val coldWaterLl = view.findViewById<LinearLayout>(R.id.ll_coldWater)
        val hotWaterLl = view.findViewById<LinearLayout>(R.id.ll_hotWater)
        val drainWaterLl = view.findViewById<LinearLayout>(R.id.ll_drainWater)
        val electricityLl = view.findViewById<LinearLayout>(R.id.ll_electricity)
        val gasLl = view.findViewById<LinearLayout>(R.id.ll_gas)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)
        val isColdWater = sharedPreferences.getBoolean(KEY_PREF_COLD_WATER, true)
        val isHotWater = sharedPreferences.getBoolean(KEY_PREF_HOT_WATER, true)
        val isDrainWater = sharedPreferences.getBoolean(KEY_PREF_DRAIN_WATER, true)
        val isElectricity = sharedPreferences.getBoolean(KEY_PREF_ELECTRICITY, true)
        val isGas = sharedPreferences.getBoolean(KEY_PREF_GAS, true)
        coldWaterLl.visibility = if (isColdWater) View.VISIBLE else View.GONE
        hotWaterLl.visibility = if (isHotWater) View.VISIBLE else View.GONE
        drainWaterLl.visibility = if (isDrainWater) View.VISIBLE else View.GONE
        electricityLl.visibility = if (isElectricity) View.VISIBLE else View.GONE
        gasLl.visibility = if (isGas) View.VISIBLE else View.GONE
    }
}