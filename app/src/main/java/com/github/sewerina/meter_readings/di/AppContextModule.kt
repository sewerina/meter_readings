package com.github.sewerina.meter_readings.di

import android.app.Application
import dagger.Module
import dagger.Provides

@Module
class AppContextModule(private val mApplication: Application) {
    @Provides
    fun application(): Application {
        return mApplication
    }
}