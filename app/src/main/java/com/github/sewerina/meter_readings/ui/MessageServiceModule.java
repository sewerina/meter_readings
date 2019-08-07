package com.github.sewerina.meter_readings.ui;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

@Module(includes = AppContextModule.class)
public class MessageServiceModule {

    @Provides
    public MessageService messageService(Application appContext) {
        return new MessageService(appContext);
    }
}
