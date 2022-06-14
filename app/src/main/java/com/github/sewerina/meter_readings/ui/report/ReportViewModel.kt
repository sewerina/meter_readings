package com.github.sewerina.meter_readings.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ReportViewModel @Inject constructor(
    var mDao: AppDao,

    @Named("reports")
    var mReportCollectionReference: CollectionReference
) : ViewModel() {
    private val mReports = MutableLiveData<MutableList<Report>>()
    val reports: LiveData<MutableList<Report>>
        get() = mReports

    fun addReport(currentHomeEntity: HomeEntity) {
        viewModelScope.launch {
            val readingEntities = mDao.getReadingsForHome(currentHomeEntity.id)
            val report = Report(readingEntities)
            val reportList = mReports.value ?: ArrayList()
            reportList.add(report)
            mReports.postValue(reportList)
            addReportInCloudFirestore(report)
        }
    }

    private suspend fun addReportInCloudFirestore(report: Report) {
        mReportCollectionReference
            .add(report)
            .await()
    }

    fun loadReports(currentHome: HomeEntity) {
        // 1. Пойти в Firebase & взять оттуда все reports для currentHome.Id
        // 2. Обновить этими reports MutableLiveData
        viewModelScope.launch {
            val reports = loadReportsFromCloudFirestore(currentHome.id)
            mReports.postValue(reports)
        }
    }

    private suspend fun loadReportsFromCloudFirestore(currentHomeId: Int): MutableList<Report> {
        val documents = mReportCollectionReference
            .whereEqualTo("homeId", currentHomeId)
            .get()
            .await()

        val reports: MutableList<Report> = ArrayList()
        for (document in documents) {
            val report = document.toObject(Report::class.java)
            reports.add(report)
        }
        return reports
    }
}