package com.github.sewerina.meter_readings

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class FormattedDate @SuppressLint("SimpleDateFormat") constructor(private val mDate: Date) {
    private val mDateFormat: SimpleDateFormat
    fun text(): String {
        return mDateFormat.format(mDate)
    }

    init {
        val pattern = "dd.MM.yyyy"
        mDateFormat = SimpleDateFormat(pattern)
    }
}