package com.github.sewerina.meter_readings.ui;

import com.github.sewerina.meter_readings.ui.backup_copying.BackupCopyingViewModel;
import com.github.sewerina.meter_readings.ui.chart.ChartViewModel;
import com.github.sewerina.meter_readings.ui.homes.HomesViewModel;
import com.github.sewerina.meter_readings.ui.readings_main.MainViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {DaoModule.class, FirestoreModule.class, MessageServiceModule.class})
@Singleton
public interface MainComponent {
    void inject(MainViewModel viewModel);
    void inject(HomesViewModel viewModel);
    void inject(BackupCopyingViewModel viewModel);
    void inject(ChartViewModel viewModel);

}
