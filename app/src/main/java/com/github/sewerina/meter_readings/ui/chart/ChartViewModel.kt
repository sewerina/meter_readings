package com.github.sewerina.meter_readings.ui.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.database.ReadingEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChartViewModel @Inject constructor(
    var mDao: AppDao
) : ViewModel() {
    private val mReadingEntities = MutableLiveData<List<ReadingEntity>>()

    private val mDisposables = CompositeDisposable()
    override fun onCleared() {
        super.onCleared()
        mDisposables.dispose()
    }

    val readings: LiveData<List<ReadingEntity>>
        get() = mReadingEntities

    fun loadInChart(currentHomeEntity: HomeEntity) {
        val subscribe = mDao.getReadingsForHomeRx(currentHomeEntity.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { readingEntities -> mReadingEntities.postValue(readingEntities) }
        mDisposables.add(subscribe)
    }
}