package com.github.sewerina.meter_readings.ui

import android.content.Context
import android.os.Handler
import android.widget.Toast

class MessageService(private val context: Context) {
    fun showMessage(strRes: Int) {
        val mainHandler = Handler(context.mainLooper)
        val myRunnable = Runnable { Toast.makeText(context, strRes, Toast.LENGTH_SHORT).show() }
        mainHandler.post(myRunnable)
    }
}