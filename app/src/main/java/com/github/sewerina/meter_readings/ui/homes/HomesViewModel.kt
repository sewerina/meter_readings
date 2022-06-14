package com.github.sewerina.meter_readings.ui.homes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.database.NewHomeEntity
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomesViewModel @Inject constructor(
    var mDao: AppDao,
    @Named("homes")
    var mCollectionReference: CollectionReference
) : ViewModel() {
    val homes: LiveData<List<HomeEntity>> = mDao.homesLiveData

    fun addHome(homeEntity: NewHomeEntity) {
        viewModelScope.launch {
            val homeId = mDao.insertHome(homeEntity)
            mCollectionReference.add(HomeEntity(homeId.toInt(), homeEntity.address))
                .await()
        }
    }

    fun deleteHome(homeEntity: HomeEntity) {
        viewModelScope.launch {
            mDao.deleteHome(homeEntity)

            val documents = mCollectionReference
                .whereEqualTo("id", homeEntity.id)
                .get()
                .await()
            for (document in documents) {
                document.reference.delete()
            }
        }
    }

    fun updateHome(homeEntity: HomeEntity) {
        viewModelScope.launch {
            mDao.updateHome(homeEntity)

            val documents = mCollectionReference
                .whereEqualTo("id", homeEntity.id)
                .get()
                .await()
            for (document in documents) {
                document.reference.set(homeEntity)
            }
        }
    }

    companion object {
        private const val TAG = "HomesViewModel"
    }
}