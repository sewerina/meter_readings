package com.github.sewerina.meter_readings.di

import com.github.sewerina.meter_readings.ui.backup_copying.BackupCopyingActivity
import com.github.sewerina.meter_readings.ui.chart.ChartActivity
import com.github.sewerina.meter_readings.ui.homes.EditHomeDialog
import com.github.sewerina.meter_readings.ui.homes.HomesActivity
import com.github.sewerina.meter_readings.ui.homes.NewHomeDialog
import com.github.sewerina.meter_readings.ui.readings_main.BottomSheetReadingDialog
import com.github.sewerina.meter_readings.ui.readings_main.EditReadingDialog
import com.github.sewerina.meter_readings.ui.readings_main.MainActivity
import com.github.sewerina.meter_readings.ui.readings_main.NewReadingDialog
import com.github.sewerina.meter_readings.ui.report.ReportActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ViewModelModule::class])
@Singleton
interface MainComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: BackupCopyingActivity)
    fun inject(activity: HomesActivity)
    fun inject(activity: ReportActivity)
    fun inject(activity: ChartActivity)
    fun inject(dialogFragment: EditHomeDialog)
    fun inject(dialogFragment: NewHomeDialog)
    fun inject(dialogFragment: BottomSheetReadingDialog)
    fun inject(dialogFragment: EditReadingDialog)
    fun inject(dialogFragment: NewReadingDialog)
}