package com.github.sewerina.meter_readings.ui;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

public class MessageService {

    private final Application mAppContext;

    public MessageService(Application context) {
        mAppContext = context;
    }

    public void showMessage(final int strRes) {
        Handler mainHandler = new Handler(mAppContext.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mAppContext, strRes, Toast.LENGTH_SHORT).show();
            }
        };
        mainHandler.post(myRunnable);
    }
}
