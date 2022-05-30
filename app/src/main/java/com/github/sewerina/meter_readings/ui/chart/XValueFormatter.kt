package com.github.sewerina.meter_readings.ui.chart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class XValueFormatter(private val mDateList: List<Date>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return super.getFormattedValue(value)
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        val pattern = "MM.yy"
        val dateFormat = SimpleDateFormat(pattern)
        val index = value.toInt()
        return if (index < 0 || index >= mDateList.size) {
            ""
        } else dateFormat.format(mDateList[index])
    }

    override fun getBarLabel(barEntry: BarEntry): String {
        return super.getBarLabel(barEntry)
    }

    override fun getBarStackedLabel(value: Float, stackedEntry: BarEntry): String {
        return super.getBarStackedLabel(value, stackedEntry)
    }

    companion object {
        private const val TAG = "XValueFormatter"
    }
}