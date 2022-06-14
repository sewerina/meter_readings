package com.github.sewerina.meter_readings.ui.selectHome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectHomeViewModel @Inject constructor(
    var mDao: AppDao
) : ViewModel() {
    val homeEntities: LiveData<List<HomeEntity>> = mDao.homesLiveData

    private val mCurrentHomePosition = MutableLiveData<Int>()
    val currentHomePosition: LiveData<Int>
        get() = mCurrentHomePosition

    var loaded: Boolean = false

    fun load() {
        loaded = true
        mCurrentHomePosition.postValue(0)
    }

    fun changeCurrentHome(position: Int) {
        mCurrentHomePosition.postValue(position)
    }

    fun getCurrentHomeEntity(): HomeEntity {
        return homeEntities.value!![mCurrentHomePosition.value!!]
    }
}