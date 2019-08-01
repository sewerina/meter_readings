package com.github.sewerina.meter_readings.ui;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

@Module
public class AppContextModule {
    private final Application mApplication;

    public AppContextModule(Application application) {
        mApplication = application;
    }

    @Provides
    public Application application() {
        return mApplication;
    }
}
