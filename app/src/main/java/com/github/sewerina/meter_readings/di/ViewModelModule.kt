package com.github.sewerina.meter_readings.di

import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.ui.MessageService
import com.github.sewerina.meter_readings.ui.backup_copying.BackupCopyingViewModel
import com.github.sewerina.meter_readings.ui.chart.ChartViewModel
import com.github.sewerina.meter_readings.ui.homes.HomesViewModel
import com.github.sewerina.meter_readings.ui.readings_main.ReadingsViewModel
import com.github.sewerina.meter_readings.ui.report.ReportViewModel
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeViewModel
import com.google.firebase.firestore.CollectionReference
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [DaoModule::class, FirestoreModule::class, MessageServiceModule::class])
class ViewModelModule {
    @Provides
    @Singleton
    fun providesMainViewModel(
        dao: AppDao,
        @Named("readings") reference: CollectionReference
    ): ReadingsViewModel {
        return ReadingsViewModel(dao, reference)
    }

    @Provides
    @Singleton
    fun providesHomesViewModel(
        dao: AppDao,
        @Named("homes") reference: CollectionReference
    ): HomesViewModel {
        return HomesViewModel(dao, reference)
    }

    @Provides
    @Singleton
    fun providesBackupCopyingViewModel(
        dao: AppDao,
        @Named("readings") readingsRef: CollectionReference,
        @Named("homes") homesRef: CollectionReference,
        messageService: MessageService
    ): BackupCopyingViewModel {
        return BackupCopyingViewModel(dao, readingsRef, homesRef, messageService)
    }

    @Provides
    @Singleton
    fun providesChartViewModel(dao: AppDao): ChartViewModel {
        return ChartViewModel(dao)
    }

    @Provides
    @Singleton
    fun providesReportViewModel(
        dao: AppDao,
        @Named("reports") reference: CollectionReference
    ): ReportViewModel {
        return ReportViewModel(dao, reference)
    }

    @Provides
    @Singleton
    fun providesSelectHomeViewModel(dao: AppDao): SelectHomeViewModel {
        return SelectHomeViewModel(dao)
    }
}