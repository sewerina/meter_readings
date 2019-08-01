package com.github.sewerina.meter_readings.ui.chart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.sewerina.meter_readings.R;
import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    private static final String EXTRA_CURRENT_HOME_ENTITY = "currentHomeEntity";
    private ChartViewModel mViewModel;
    private HomeEntity mCurrentHomeEntity;
    private BarChart mChart;

    public static Intent newIntent(Context context, HomeEntity homeEntity) {
        Intent intent = new Intent(context, ChartActivity.class);
        intent.putExtra(EXTRA_CURRENT_HOME_ENTITY, homeEntity);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mChart = findViewById(R.id.chart);
        mChart.setNoDataText(getResources().getString(R.string.chart_noDataText));
        mChart.setNoDataTextColor(Color.BLUE);
        mChart.setNoDataTextTypeface(Typeface.SERIF);

        if (getIntent() != null) {
            mCurrentHomeEntity = (HomeEntity) getIntent().getSerializableExtra(EXTRA_CURRENT_HOME_ENTITY);
        }

        setTitle(mCurrentHomeEntity.address);

        mViewModel = ViewModelProviders.of(this).get(ChartViewModel.class);
        ReadingApp.sMainComponent.inject(mViewModel);

        mViewModel.getReadings().observe(this, new Observer<List<ReadingEntity>>() {
            @Override
            public void onChanged(List<ReadingEntity> readingEntities) {
                if (readingEntities != null && !readingEntities.isEmpty()) {
                    showChart(readingEntities);
                }
            }
        });
        mViewModel.loadInChart(mCurrentHomeEntity);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showChart(List<ReadingEntity> readings) {
        List<Date> dates = new ArrayList<>();
        for (ReadingEntity reading : readings) {
            dates.add(reading.date);
        }

        XAxis xAxis = mChart.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(readings.size());
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setEnabled(true);
//        xAxis.setDrawLabels(true);
//        xAxis.setDrawAxisLine(true);
//        xAxis.setDrawGridLines(true);
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new XValueFormatter(dates));

        YAxis leftYAxis = mChart.getAxisLeft();
        YAxis rightYAxis = mChart.getAxisRight();
        leftYAxis.setAxisMinimum(0);
        rightYAxis.setAxisMinimum(0);
//        leftYAxis.mAxisMinimum = 0;
//        rightYAxis.mAxisMinimum = 0;
//        leftYAxis.setDrawZeroLine(true);
        leftYAxis.setEnabled(true);
        rightYAxis.setEnabled(true);
//        leftYAxis.setDrawLabels(true);
//        rightYAxis.setDrawLabels(true);
//        leftYAxis.setDrawAxisLine(true);
//        rightYAxis.setDrawAxisLine(true);
//        leftYAxis.setDrawGridLines(true);
//        rightYAxis.setDrawGridLines(true);
        leftYAxis.setTextSize(12f);
        rightYAxis.setTextSize(12f);

        Legend legend = mChart.getLegend();
        legend.setFormSize(10f);
        legend.setTextSize(12f);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        legend.setXEntrySpace(10f);
        legend.setYEntrySpace(10f);

        mChart.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        Description description = new Description();
        description.setText(getResources().getString(R.string.chart_description));
        description.setTextSize(14f);
//        description.setTextAlign(Paint.Align.RIGHT);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int displayWidth = metrics.widthPixels;
        int displayHeight = metrics.heightPixels;
        description.setPosition((float) displayWidth * 2/3, 90);
        mChart.setDescription(description);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isColdWater = preferences.getBoolean(ReadingPreferences.KEY_PREF_COLD_WATER, true);
        boolean isHotWater = preferences.getBoolean(ReadingPreferences.KEY_PREF_HOT_WATER, true);
        boolean isDrainWater = preferences.getBoolean(ReadingPreferences.KEY_PREF_DRAIN_WATER, true);
        boolean isElectricity = preferences.getBoolean(ReadingPreferences.KEY_PREF_ELECTRICITY, true);
        boolean isGas = preferences.getBoolean(ReadingPreferences.KEY_PREF_GAS, true);

        int n = 0;
        List<IBarDataSet> dataSets = new ArrayList<>();
        if (isColdWater) {
            List<BarEntry> entriesGroupColdWater = new ArrayList<>();
            for (int i = 0; i < readings.size(); i++) {
                ReadingEntity reading = readings.get(i);
                entriesGroupColdWater.add(new BarEntry(i, reading.coldWater));
            }
            BarDataSet setColdWater = new BarDataSet(entriesGroupColdWater, getResources().getString(R.string.label_coldWater));
            setColdWater.setColor(Color.BLUE);
            dataSets.add(setColdWater);
            n++;
        }
        if (isHotWater) {
            List<BarEntry> entriesGroupHotWater = new ArrayList<>();
            for (int i = 0; i < readings.size(); i++) {
                ReadingEntity reading = readings.get(i);
                entriesGroupHotWater.add(new BarEntry(i, reading.hotWater));
            }
            BarDataSet setHotWater = new BarDataSet(entriesGroupHotWater, getResources().getString(R.string.label_hotWater));
            setHotWater.setColor(Color.RED);
            dataSets.add(setHotWater);
            n++;
        }
        if (isDrainWater) {
            List<BarEntry> entriesGroupDrainWater = new ArrayList<>();
            for (int i = 0; i < readings.size(); i++) {
                ReadingEntity reading = readings.get(i);
                entriesGroupDrainWater.add(new BarEntry(i, reading.drainWater));
            }
            BarDataSet setDrainWater = new BarDataSet(entriesGroupDrainWater, getResources().getString(R.string.label_drainWater));
            setDrainWater.setColor(Color.DKGRAY);
            dataSets.add(setDrainWater);
            n++;
        }
        if (isElectricity) {
            List<BarEntry> entriesGroupElectricity = new ArrayList<>();
            for (int i = 0; i < readings.size(); i++) {
                ReadingEntity reading = readings.get(i);
                entriesGroupElectricity.add(new BarEntry(i, reading.electricity));
            }
            BarDataSet setElectricity = new BarDataSet(entriesGroupElectricity, getResources().getString(R.string.label_electricity));
            setElectricity.setColor(Color.YELLOW);
            dataSets.add(setElectricity);
            n++;
        }
        if (isGas) {
            List<BarEntry> entriesGroupGas = new ArrayList<>();
            for (int i = 0; i < readings.size(); i++) {
                ReadingEntity reading = readings.get(i);
                entriesGroupGas.add(new BarEntry(i, reading.gas));
            }
            BarDataSet setGas = new BarDataSet(entriesGroupGas, getResources().getString(R.string.label_gas));
            setGas.setColor(Color.CYAN);
            dataSets.add(setGas);
            n++;
        }
        Log.d("ChartActivity", "showChart: n = " + n);

        float groupSpace = 0;
        float barSpace = 0;
        float barWidth = 0;

        BarData data = new BarData(dataSets);
        data.setValueTextSize(12);
        mChart.setData(data);
        if (n > 1) {
            groupSpace = 0.1f;
            float b = (1.00f - groupSpace)/n;
            barWidth = b * 0.9f;
            barSpace = b - barWidth;
            data.setBarWidth(barWidth);
            mChart.groupBars(0f, groupSpace, barSpace); // perform the "explicit"
            mChart.notifyDataSetChanged();
        } else {
            barWidth = 0.25f;
            data.setBarWidth(barWidth);
        }

        mChart.invalidate(); // refresh
    }
}
