package com.github.sewerina.meter_readings.ui.report

import android.view.View
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeViewModel

class ReportHolder(itemView: View, private val mSelectHomeVM: SelectHomeViewModel) :
    RecyclerView.ViewHolder(itemView) {
    private val mMessageTv: TextView = itemView as TextView
    private lateinit var mReport: Report
    fun bind(report: Report) {
        mReport = report
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)
        val isColdWater =
            sharedPreferences.getBoolean(ReadingPreferences.KEY_PREF_COLD_WATER, true)
        val isHotWater =
            sharedPreferences.getBoolean(ReadingPreferences.KEY_PREF_HOT_WATER, true)
        val isDrainWater =
            sharedPreferences.getBoolean(ReadingPreferences.KEY_PREF_DRAIN_WATER, true)
        val isElectricity =
            sharedPreferences.getBoolean(ReadingPreferences.KEY_PREF_ELECTRICITY, true)
        val isGas = sharedPreferences.getBoolean(ReadingPreferences.KEY_PREF_GAS, true)
        val message = mReport.reportMessage(
            isColdWater,
            isHotWater,
            isDrainWater,
            isElectricity,
            isGas,
            mSelectHomeVM.getCurrentHomeEntity().address
        )
        mMessageTv.text = message
    }
}