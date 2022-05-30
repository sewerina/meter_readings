package com.github.sewerina.meter_readings.ui

import android.app.Application
import android.os.Handler
import android.widget.Toast

class MessageService(private val mAppContext: Application) {
    fun showMessage(strRes: Int) {
        val mainHandler = Handler(mAppContext.mainLooper)
        val myRunnable = Runnable { Toast.makeText(mAppContext, strRes, Toast.LENGTH_SHORT).show() }
        mainHandler.post(myRunnable)
    }
}