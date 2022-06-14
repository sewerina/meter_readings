package com.github.sewerina.meter_readings.ui.readings_main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.database.NewReadingEntity
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ReadingsViewModel @Inject constructor(
    var mDao: AppDao,

    @Named("readings")
    var mCollectionReference: CollectionReference
) : ViewModel() {
    private val mReadingEntities = MutableLiveData<List<ReadingEntity>>()
    val readings: LiveData<List<ReadingEntity>>
        get() = mReadingEntities

    fun addReading(currentHomeEntity: HomeEntity, newReadingEntity: NewReadingEntity) {
        viewModelScope.launch {
            newReadingEntity.homeId = currentHomeEntity.id
            val readingId = mDao.insertReading(newReadingEntity)
            mCollectionReference
                .add(newReadingEntity.toEntity(readingId.toInt()))
                .await()
            loadReadingsAsync(currentHomeEntity)
        }
    }

    fun deleteReading(currentHomeEntity: HomeEntity, readingEntity: ReadingEntity) {
        viewModelScope.launch {
            mDao.deleteReading(readingEntity)
            val documents = mCollectionReference
                .whereEqualTo("id", readingEntity.id)
                .get()
                .await()
            for (document in documents) {
                document.reference.delete()
            }
            loadReadingsAsync(currentHomeEntity)
        }
    }

    fun updateReading(currentHomeEntity: HomeEntity, readingEntity: ReadingEntity) {
        viewModelScope.launch {
            mDao.updateReading(readingEntity)
            val documents = mCollectionReference
                .whereEqualTo("id", readingEntity.id)
                .get()
                .await()
            for (document in documents) {
                document.reference.set(readingEntity)
            }
            loadReadingsAsync(currentHomeEntity)
        }
    }

    fun loadReadings(homeEntity: HomeEntity) {
        viewModelScope.launch {
            loadReadingsAsync(homeEntity)
        }
    }

    private suspend fun loadReadingsAsync(currentHomeEntity: HomeEntity) {
        val readingEntities = mDao.getReadingsForHome(currentHomeEntity.id)
        mReadingEntities.postValue(readingEntities)
    }
}