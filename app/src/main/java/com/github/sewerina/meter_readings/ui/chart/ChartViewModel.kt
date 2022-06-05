package com.github.sewerina.meter_readings.ui.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.database.ReadingEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChartViewModel @Inject constructor(
    var mDao: AppDao
) : ViewModel() {
    private val mReadingEntities = MutableLiveData<List<ReadingEntity>>()
    val readings: LiveData<List<ReadingEntity>>
        get() = mReadingEntities

    fun loadInChart(currentHomeEntity: HomeEntity) {
        viewModelScope.launch {
            val readingEntities = mDao.getReadingsForHome(currentHomeEntity.id)
            mReadingEntities.postValue(readingEntities)
        }
    }
}