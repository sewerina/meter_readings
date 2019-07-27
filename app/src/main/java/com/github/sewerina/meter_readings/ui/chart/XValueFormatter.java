package com.github.sewerina.meter_readings.ui.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class XValueFormatter extends ValueFormatter {
    private static final String TAG = "XValueFormatter";
    private List<Date> mDateList;

    public XValueFormatter(List<Date> dateList) {
        super();
        mDateList = dateList;
    }

    @Override
    public String getFormattedValue(float value) {
        return super.getFormattedValue(value);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        String pattern = "MM.yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        int index = (int) value;
        if (index < 0 || index >= mDateList.size()) {
            return "";
        }
        return dateFormat.format(mDateList.get(index));
    }

    @Override
    public String getBarLabel(BarEntry barEntry) {
        return super.getBarLabel(barEntry);
    }

    @Override
    public String getBarStackedLabel(float value, BarEntry stackedEntry) {
        return super.getBarStackedLabel(value, stackedEntry);
    }
}
