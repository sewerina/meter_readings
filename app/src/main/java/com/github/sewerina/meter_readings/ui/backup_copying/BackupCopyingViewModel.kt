package com.github.sewerina.meter_readings.ui.backup_copying

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.github.sewerina.meter_readings.ui.MessageService
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class BackupCopyingViewModel @Inject constructor(
    var mDao: AppDao,

    @Named("readings")
    var mReferenceReadings: CollectionReference,

    @Named("homes")
    var mReferenceHomes: CollectionReference,

    var mMessageService: MessageService
) : ViewModel() {
    private val mIsRefreshing = MutableLiveData(false)
    private val mIsAvailable = MutableLiveData(true)

    val isRefreshing: LiveData<Boolean>
        get() = mIsRefreshing
    val isAvailable: LiveData<Boolean>
        get() = mIsAvailable

    fun backup() {
        // 1. Удалить данные из коллекции "homes" в CloudFirestore
        // 2. Удалить данные из коллекции "readings" в CloudFirestore
        // 3. Получить все readings из БД
        // 4. Отправить все readings в CloudFirestore
        // 5. Получить все homes из БД
        // 6. Отправить все homes в CloudFirestore
        viewModelScope.launch {
            mIsRefreshing.postValue(true)
            mIsAvailable.postValue(false)

            try {
                deleteHomes()
                deleteReadings()

                val readings = mDao.allReadings()
                backupAllReadings(readings)

                val homes = mDao.homes()
                backupAllHomes(homes)

                mMessageService.showMessage(R.string.successful_backup)
            } catch (e: Exception) {
                mMessageService.showMessage(R.string.failed_backup)
            }

            mIsRefreshing.postValue(false)
            mIsAvailable.postValue(true)
        }
    }

    private suspend fun deleteHomes() {
        val documents = mReferenceHomes
            .get()
            .await()
        for (document in documents) {
            document.reference.delete()
        }
    }

    private suspend fun deleteReadings() {
        val documents = mReferenceReadings
            .get()
            .await()
        for (document in documents) {
            document.reference.delete()
        }
    }

    private suspend fun backupAllReadings(readingEntities: List<ReadingEntity>) {
        for (reading in readingEntities) {
            mReferenceReadings
                .add(reading)
                .await()
        }
    }

    private suspend fun backupAllHomes(homeEntities: List<HomeEntity>) {
        for (home in homeEntities) {
            mReferenceHomes
                .add(home)
                .await()
        }
    }

    fun restoreDb() {
        // 1. Очистить в БД таблицу с именем "reading"
        // 2. Очистить в БД таблицу с именем "home"
        // 3. Обновить таблицу с именем "home"
        // 4. Обновить таблицу с именем "reading"
        viewModelScope.launch {
            mIsRefreshing.postValue(true)
            mIsAvailable.postValue(false)

            try {
                mDao.clearTableReading()
                mDao.clearTableHome()
                val homeEntities = loadHomes()
                mDao.insertInTableHome(homeEntities)
                val readingEntities = loadReadings()
                mDao.insertInTableReading(readingEntities)

                mMessageService.showMessage(R.string.successful_restoration)
            } catch (e: Exception) {
                mMessageService.showMessage(R.string.unsuccessful_restoration)
            }

            mIsRefreshing.postValue(false)
            mIsAvailable.postValue(true)
        }
    }

    private suspend fun loadHomes(): List<HomeEntity> {
        val homeEntities: MutableList<HomeEntity> = ArrayList()
        val documents = mReferenceHomes
            .get()
            .await()
        for (document in documents) {
            val home = document.toObject(HomeEntity::class.java)
            homeEntities.add(home)
        }
        return homeEntities
    }

    private suspend fun loadReadings(): List<ReadingEntity> {
        val readingEntities: MutableList<ReadingEntity> = ArrayList()
        val documents = mReferenceReadings
            .get()
            .await()
        for (document in documents) {
            val reading = document.toObject(
                ReadingEntity::class.java
            )
            readingEntities.add(reading)
        }
        return readingEntities
    }

    companion object {
        private const val TAG = "BackupCopyingViewModel"
    }

}