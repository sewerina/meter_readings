package com.github.sewerina.meter_readings.di

import com.github.sewerina.meter_readings.ui.backup_copying.BackupCopyingFragment
import com.github.sewerina.meter_readings.ui.chart.ChartFragment
import com.github.sewerina.meter_readings.ui.homes.EditHomeDialog
import com.github.sewerina.meter_readings.ui.homes.HomesFragment
import com.github.sewerina.meter_readings.ui.homes.NewHomeDialog
import com.github.sewerina.meter_readings.ui.readings_main.BottomSheetReadingDialog
import com.github.sewerina.meter_readings.ui.readings_main.EditReadingDialog
import com.github.sewerina.meter_readings.ui.readings_main.NewReadingDialog
import com.github.sewerina.meter_readings.ui.readings_main.ReadingsFragment
import com.github.sewerina.meter_readings.ui.report.ReportsFragment
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ViewModelModule::class])
@Singleton
interface MainComponent {
    fun inject(activity: ReadingsFragment)
    fun inject(activity: HomesFragment)
    fun inject(activity: ChartFragment)
    fun inject(activity: ReportsFragment)
    fun inject(activity: SelectHomeFragment)
    fun inject(activity: BackupCopyingFragment)
    fun inject(dialogFragment: EditHomeDialog)
    fun inject(dialogFragment: NewHomeDialog)
    fun inject(dialogFragment: BottomSheetReadingDialog)
    fun inject(dialogFragment: EditReadingDialog)
    fun inject(dialogFragment: NewReadingDialog)
}