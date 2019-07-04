package com.github.sewerina.meter_readings;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    private MainViewModel mViewModel;
    private BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mChart = findViewById(R.id.chart);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getReadings().observe(this, new Observer<List<ReadingEntity>>() {
            @Override
            public void onChanged(List<ReadingEntity> readingEntities) {
                if (readingEntities != null && !readingEntities.isEmpty()) {
                    showChart(readingEntities);
                }
            }
        });
        mViewModel.load();

    }

    private void showChart(List<ReadingEntity> readings) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setCenterAxisLabels(true);

        mChart.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        Description description = new Description();
        description.setText("Данные по месяцам");
        description.setTextAlign(Paint.Align.RIGHT);
        mChart.setDescription(description);

        List<BarEntry> entriesGroupColdWater = new ArrayList<>();
        List<BarEntry> entriesGroupHotWater = new ArrayList<>();
        List<BarEntry> entriesGroupDrainWater = new ArrayList<>();
        List<BarEntry> entriesGroupElectricity = new ArrayList<>();
        List<BarEntry> entriesGroupGas = new ArrayList<>();
        for (int i = 0; i < readings.size(); i++) {
            ReadingEntity reading = readings.get(i);
            entriesGroupColdWater.add(new BarEntry(i, reading.coldWater));
            entriesGroupHotWater.add(new BarEntry(i, reading.hotWater));
            entriesGroupDrainWater.add(new BarEntry(i, reading.drainWater));
            entriesGroupElectricity.add(new BarEntry(i, reading.electricity));
            entriesGroupGas.add(new BarEntry(i, reading.gas));
        }

        BarDataSet setColdWater = new BarDataSet(entriesGroupColdWater, "Cold water");
        setColdWater.setColor(Color.BLUE);

        BarDataSet setHotWater = new BarDataSet(entriesGroupHotWater, "Hot water");
        setHotWater.setColor(Color.RED);

        BarDataSet setDrainWater = new BarDataSet(entriesGroupDrainWater, "Drain water");
        setDrainWater.setColor(Color.DKGRAY);

        BarDataSet setElectricity = new BarDataSet(entriesGroupElectricity, "Electricity");
        setElectricity.setColor(Color.YELLOW);

        BarDataSet setGas = new BarDataSet(entriesGroupGas, "Gas");
        setGas.setColor(Color.CYAN);

        BarData data = new BarData(setColdWater, setHotWater, setDrainWater, setElectricity, setGas);
        float groupSpace = 0.05f;
        float barSpace = 0.02f; // x5 dataset
        float barWidth = 0.17f; // x5 dataset
// (0.02 + 0.17) * 5 + 0.05 = 1.00 -> interval per "group"
        data.setBarWidth(barWidth); // set the width of each
        mChart.setData(data);
        float fromX = readings.get(0).date.getTime();
        mChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
        mChart.invalidate(); // refresh
    }
}
