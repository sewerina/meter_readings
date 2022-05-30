package com.github.sewerina.meter_readings.di

import android.app.Application
import com.github.sewerina.meter_readings.ui.MessageService
import dagger.Module
import dagger.Provides

@Module(includes = [AppContextModule::class])
class MessageServiceModule {
    @Provides
    fun messageService(appContext: Application): MessageService {
        return MessageService(appContext)
    }
}