package com.github.sewerina.meter_readings.ui.homes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.database.NewHomeEntity
import com.google.firebase.firestore.CollectionReference
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

class HomesViewModel @Inject constructor(
    var mDao: AppDao,
    @Named("homes")
    var mCollectionReference: CollectionReference
) : ViewModel() {
    private val mDisposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        mDisposables.dispose()
    }

     val homes: LiveData<List<HomeEntity>> = mDao.homesLiveData

    fun addHome(homeEntity: NewHomeEntity) {
        val subscribe = mDao.insertHomeRx(homeEntity)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { homeId ->
                addHomeInCloudFirestore(
                    HomeEntity(
                        homeId.toInt(),
                        homeEntity.address
                    )
                )
            }
            .subscribe()
        mDisposables.add(subscribe)
    }

    private fun addHomeInCloudFirestore(homeEntity: HomeEntity): Completable {
        return Completable.create { emitter ->
            mCollectionReference
                .add(homeEntity)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.id)
                    emitter.onComplete()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error adding document", e)
                    emitter.onError(Exception())
                }
        }
    }

    fun deleteHome(homeEntity: HomeEntity) {
        val subscribe = mDao.deleteHomeRx(homeEntity)
            .subscribeOn(Schedulers.io())
            .andThen(deleteHomeFromCloudFirestore(homeEntity))
            .subscribe()
        mDisposables.add(subscribe)
    }

    private fun deleteHomeFromCloudFirestore(homeEntity: HomeEntity): Completable {
        return Completable.create { emitter ->
            mCollectionReference
                .whereEqualTo("id", homeEntity.id)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        for (document in task.result!!) {
                            document.reference.delete()
                        }
                        emitter.onComplete()
                    } else {
                        emitter.onError(Exception())
                    }
                }
        }
    }

    fun updateHome(homeEntity: HomeEntity) {
        val subscribe = mDao.updateHomeRx(homeEntity)
            .subscribeOn(Schedulers.io())
            .andThen(updateHomeInCloudFirestore(homeEntity))
            .subscribe()
        mDisposables.add(subscribe)
    }

    private fun updateHomeInCloudFirestore(homeEntity: HomeEntity): Completable {
        return Completable.create { emitter ->
            mCollectionReference
                .whereEqualTo("id", homeEntity.id)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        for (document in task.result!!) {
                            document.reference.set(homeEntity)
                        }
                        emitter.onComplete()
                    } else {
                        emitter.onError(Exception())
                    }
                }
        }
    }

    companion object {
        private const val TAG = "HomesViewModel"
    }
}