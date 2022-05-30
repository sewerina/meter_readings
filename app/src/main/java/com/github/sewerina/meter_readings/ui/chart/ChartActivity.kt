package com.github.sewerina.meter_readings.ui.chart

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences
import java.util.*
import javax.inject.Inject

class ChartActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModel: ChartViewModel
    private lateinit var mCurrentHomeEntity: HomeEntity
    private lateinit var mChart: BarChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        mChart = findViewById(R.id.chart)
        mChart.setNoDataText(resources.getString(R.string.chart_noDataText))
        mChart.setNoDataTextColor(Color.BLUE)
        mChart.setNoDataTextTypeface(Typeface.SERIF)
        if (intent != null) {
            mCurrentHomeEntity =
                intent.getSerializableExtra(EXTRA_CURRENT_HOME_ENTITY) as HomeEntity
        }
        title = mCurrentHomeEntity.address

        ReadingApp.sMainComponent!!.inject(this)
        mViewModel.readings.observe(this) { readingEntities ->
            if (readingEntities != null && readingEntities.isNotEmpty()) {
                showChart(readingEntities)
            }
        }
        mViewModel.loadInChart(mCurrentHomeEntity)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showChart(readings: List<ReadingEntity>) {
        val dates: MutableList<Date> = ArrayList()
        for (reading in readings) {
            dates.add(reading.date)
        }
        val xAxis = mChart.xAxis
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = readings.size.toFloat()
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.isEnabled = true
        //        xAxis.setDrawLabels(true);
//        xAxis.setDrawAxisLine(true);
//        xAxis.setDrawGridLines(true);
        xAxis.textSize = 12f
        xAxis.valueFormatter = XValueFormatter(dates)
        val leftYAxis = mChart.axisLeft
        val rightYAxis = mChart.axisRight
        leftYAxis.axisMinimum = 0f
        rightYAxis.axisMinimum = 0f
        //        leftYAxis.mAxisMinimum = 0;
//        rightYAxis.mAxisMinimum = 0;
//        leftYAxis.setDrawZeroLine(true);
        leftYAxis.isEnabled = true
        rightYAxis.isEnabled = true
        //        leftYAxis.setDrawLabels(true);
//        rightYAxis.setDrawLabels(true);
//        leftYAxis.setDrawAxisLine(true);
//        rightYAxis.setDrawAxisLine(true);
//        leftYAxis.setDrawGridLines(true);
//        rightYAxis.setDrawGridLines(true);
        leftYAxis.textSize = 12f
        rightYAxis.textSize = 12f
        val legend = mChart.legend
        legend.formSize = 10f
        legend.textSize = 12f
        legend.setDrawInside(false)
        legend.isWordWrapEnabled = true
        legend.xEntrySpace = 10f
        legend.yEntrySpace = 10f
        mChart.setBackgroundColor(resources.getColor(R.color.colorBackground, null))
        val description = Description()
        description.text = resources.getString(R.string.chart_description)
        description.textSize = 14f
        //        description.setTextAlign(Paint.Align.RIGHT);
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val displayWidth = metrics.widthPixels
        val displayHeight = metrics.heightPixels
        description.setPosition(displayWidth.toFloat() * 2 / 3, 90f)
        mChart.description = description
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isColdWater = preferences.getBoolean(ReadingPreferences.KEY_PREF_COLD_WATER, true)
        val isHotWater = preferences.getBoolean(ReadingPreferences.KEY_PREF_HOT_WATER, true)
        val isDrainWater = preferences.getBoolean(ReadingPreferences.KEY_PREF_DRAIN_WATER, true)
        val isElectricity = preferences.getBoolean(ReadingPreferences.KEY_PREF_ELECTRICITY, true)
        val isGas = preferences.getBoolean(ReadingPreferences.KEY_PREF_GAS, true)
        var n = 0
        val dataSets: MutableList<IBarDataSet> = ArrayList()
        if (isColdWater) {
            val entriesGroupColdWater: MutableList<BarEntry> = ArrayList()
            for (i in readings.indices) {
                val reading = readings[i]
                entriesGroupColdWater.add(BarEntry(i.toFloat(), reading.coldWater.toFloat()))
            }
            val setColdWater =
                BarDataSet(entriesGroupColdWater, resources.getString(R.string.label_coldWater))
            setColdWater.color = Color.BLUE
            dataSets.add(setColdWater)
            n++
        }
        if (isHotWater) {
            val entriesGroupHotWater: MutableList<BarEntry> = ArrayList()
            for (i in readings.indices) {
                val reading = readings[i]
                entriesGroupHotWater.add(BarEntry(i.toFloat(), reading.hotWater.toFloat()))
            }
            val setHotWater =
                BarDataSet(entriesGroupHotWater, resources.getString(R.string.label_hotWater))
            setHotWater.color = Color.RED
            dataSets.add(setHotWater)
            n++
        }
        if (isDrainWater) {
            val entriesGroupDrainWater: MutableList<BarEntry> = ArrayList()
            for (i in readings.indices) {
                val reading = readings[i]
                entriesGroupDrainWater.add(BarEntry(i.toFloat(), reading.drainWater.toFloat()))
            }
            val setDrainWater =
                BarDataSet(entriesGroupDrainWater, resources.getString(R.string.label_drainWater))
            setDrainWater.color = Color.DKGRAY
            dataSets.add(setDrainWater)
            n++
        }
        if (isElectricity) {
            val entriesGroupElectricity: MutableList<BarEntry> = ArrayList()
            for (i in readings.indices) {
                val reading = readings[i]
                entriesGroupElectricity.add(BarEntry(i.toFloat(), reading.electricity.toFloat()))
            }
            val setElectricity =
                BarDataSet(entriesGroupElectricity, resources.getString(R.string.label_electricity))
            setElectricity.color = Color.YELLOW
            dataSets.add(setElectricity)
            n++
        }
        if (isGas) {
            val entriesGroupGas: MutableList<BarEntry> = ArrayList()
            for (i in readings.indices) {
                val reading = readings[i]
                entriesGroupGas.add(BarEntry(i.toFloat(), reading.gas.toFloat()))
            }
            val setGas = BarDataSet(entriesGroupGas, resources.getString(R.string.label_gas))
            setGas.color = Color.CYAN
            dataSets.add(setGas)
            n++
        }
        Log.d("ChartActivity", "showChart: n = $n")
        var groupSpace = 0f
        var barSpace = 0f
        var barWidth = 0f
        val data = BarData(dataSets)
        data.setValueTextSize(12f)
        mChart.data = data
        if (n > 1) {
            groupSpace = 0.1f
            val b = (1.00f - groupSpace) / n
            barWidth = b * 0.9f
            barSpace = b - barWidth
            data.barWidth = barWidth
            mChart.groupBars(0f, groupSpace, barSpace) // perform the "explicit"
            mChart.notifyDataSetChanged()
        } else {
            barWidth = 0.25f
            data.barWidth = barWidth
        }
        mChart.invalidate() // refresh
    }

    companion object {
        private const val EXTRA_CURRENT_HOME_ENTITY = "currentHomeEntity"

        @JvmStatic
        fun newIntent(context: Context, homeEntity: HomeEntity): Intent {
            val intent = Intent(context, ChartActivity::class.java)
            intent.putExtra(EXTRA_CURRENT_HOME_ENTITY, homeEntity)
            return intent
        }
    }
}