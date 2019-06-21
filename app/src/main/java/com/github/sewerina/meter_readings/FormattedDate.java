package com.github.sewerina.meter_readings;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormattedDate {
    private final Date mDate;
    private final SimpleDateFormat mDateFormat;

    @SuppressLint("SimpleDateFormat")
    public FormattedDate(Date date) {
        mDate = date;
        String pattern = "dd.MM.yyyy";
        mDateFormat = new SimpleDateFormat(pattern);
    }

    public String text() {
        return mDateFormat.format(mDate);
    }
}
