package com.github.sewerina.meter_readings.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import com.google.firebase.firestore.CollectionReference
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

class ReportViewModel @Inject constructor(
    var mDao: AppDao,

    @Named("reports")
    var mReportCollectionReference: CollectionReference
) : ViewModel() {
    private val mDisposables = CompositeDisposable()

    private val mReports = MutableLiveData<MutableList<Report>>()
    val reports: LiveData<MutableList<Report>>
        get() = mReports

    fun addReport(currentHomeEntity: HomeEntity) {
        val subscribe = mDao.getReadingsForHomeRx(currentHomeEntity.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { readingEntities ->
                Single.fromCallable {
                    val report = Report(readingEntities)
                    val reportList = mReports.value ?: ArrayList()
                    reportList.add(report)
                    mReports.postValue(reportList)
                    report
                }
            }
            .flatMapCompletable { report -> addReportInCloudFirestore(report) }
            .subscribe()
        mDisposables.add(subscribe)
    }

    private fun addReportInCloudFirestore(report: Report): Completable {
        return Completable.create { emitter ->
            mReportCollectionReference
                .add(report)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(Exception()) }
        }
    }

    fun loadReports(currentHome: HomeEntity) {
        // 1. Пойти в Firebase & взять оттуда все reports для currentHome.Id
        // 2. Обновить этими reports MutableLiveData
        val subscribe = loadReportsFromCloudFirestore(currentHome.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess(Consumer<MutableList<Report>> { reports -> mReports.postValue(reports) })
            .subscribe()
        mDisposables.add(subscribe)
    }

    private fun loadReportsFromCloudFirestore(currentHomeId: Int): Single<MutableList<Report>> {
        return Single.create { emitter ->
            mReportCollectionReference
                .whereEqualTo("homeId", currentHomeId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val reports: MutableList<Report> = ArrayList()
                        for (document in task.result!!) {
                            val report = document.toObject(Report::class.java)
                            reports.add(report)
                        }
                        emitter.onSuccess(reports)
                    } else {
                        emitter.onError(Exception())
                    }
                }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mDisposables.dispose()
    }
}