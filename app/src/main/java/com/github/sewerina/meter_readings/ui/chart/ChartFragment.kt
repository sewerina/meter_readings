package com.github.sewerina.meter_readings.ui.chart

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.github.sewerina.meter_readings.databinding.FragmentChartBinding
import com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mChartVM: ChartViewModel by viewModels()

    private val mSelectHomeVM: SelectHomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chart.apply {
            setNoDataText(getString(R.string.chart_noDataText))
            setNoDataTextColor(Color.BLUE)
            setNoDataTextTypeface(Typeface.DEFAULT_BOLD)
        }

        mChartVM.readings.observe(viewLifecycleOwner) { readingEntities ->
            if (readingEntities != null && readingEntities.isNotEmpty()) {
                showChart(readingEntities)
            } else {
                binding.chart.data = null
                binding.chart.setBackgroundColor(
                    resources.getColor(
                        android.R.color.background_light,
                        null
                    )
                )
                binding.chart.invalidate() // refresh
            }
        }

        mSelectHomeVM.currentHomePosition.observe(viewLifecycleOwner) {
            mChartVM.loadInChart(mSelectHomeVM.getCurrentHomeEntity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showChart(readings: List<ReadingEntity>) {
        val dates: MutableList<Date> = ArrayList()
        for (reading in readings) {
            dates.add(reading.date)
        }
        val xAxis = binding.chart.xAxis
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
        val leftYAxis = binding.chart.axisLeft
        val rightYAxis = binding.chart.axisRight
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
        val legend = binding.chart.legend
        legend.formSize = 10f
        legend.textSize = 12f
        legend.setDrawInside(false)
        legend.isWordWrapEnabled = true
        legend.xEntrySpace = 10f
        legend.yEntrySpace = 10f
        binding.chart.setBackgroundColor(resources.getColor(R.color.colorBackground, null))
        val description = Description()
        description.text = resources.getString(R.string.chart_description)
        description.textSize = 14f
        //        description.setTextAlign(Paint.Align.RIGHT);
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val displayWidth = metrics.widthPixels
        val displayHeight = metrics.heightPixels
        description.setPosition(displayWidth.toFloat() * 2 / 3, 90f)
        binding.chart.description = description
        val preferences = PreferenceManager.getDefaultSharedPreferences(binding.chart.context)
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
        binding.chart.data = data
        if (n > 1) {
            groupSpace = 0.1f
            val b = (1.00f - groupSpace) / n
            barWidth = b * 0.9f
            barSpace = b - barWidth
            data.barWidth = barWidth
            binding.chart.groupBars(0f, groupSpace, barSpace) // perform the "explicit"
            binding.chart.notifyDataSetChanged()
        } else {
            barWidth = 0.25f
            data.barWidth = barWidth
        }
        binding.chart.invalidate() // refresh
    }
}