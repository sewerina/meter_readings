package com.github.sewerina.meter_readings.ui.backup_copying

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.github.sewerina.meter_readings.ui.MessageService
import com.google.firebase.firestore.CollectionReference
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
    private val mDisposables = CompositeDisposable()

    private val mIsRefreshing = MutableLiveData(false)
    private val mIsAvailable = MutableLiveData(true)

    val isRefreshing: LiveData<Boolean>
        get() = mIsRefreshing
    val isAvailable: LiveData<Boolean>
        get() = mIsAvailable

    override fun onCleared() {
        super.onCleared()
        mDisposables.dispose()
    }

    fun backup() {
        // 1. Удалить данные из коллекции "homes" в CloudFirestore
        // 2. Удалить данные из коллекции "readings" в CloudFirestore
        // 3. Получить все readings из БД
        // 4. Отправить все readings в CloudFirestore
        // 5. Получить все homes из БД
        // 6. Отправить все homes в CloudFirestore
        val subscribe = deleteHomes()
            .subscribeOn(Schedulers.io())
            .andThen(deleteReadings())
            .andThen(mDao.allReadingsRx.subscribeOn(Schedulers.io()))
            .flatMapCompletable { readingEntities -> backupAllReadings(readingEntities) }.andThen(
                mDao.homesRx.subscribeOn(
                    Schedulers.io()
                )
            )
            .flatMapCompletable { homeEntities -> backupAllHomes(homeEntities) }
            .doOnSubscribe {
                mIsRefreshing.postValue(true)
                mIsAvailable.postValue(false)
            }
            .doFinally {
                mIsRefreshing.postValue(false)
                mIsAvailable.postValue(true)
            }
            .doOnComplete {
                mMessageService.showMessage(R.string.successful_backup)
            }
            .doOnError {
                mMessageService.showMessage(R.string.failed_backup)
            }
            .subscribe()
        mDisposables.add(subscribe)
    }

    private fun deleteHomes(): Completable {
        return Completable.create { emitter ->
            mReferenceHomes
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        for (document in task.result!!) {
                            document.reference.delete()
                            Log.d(TAG, document.id + " => " + document.data)
                        }
                        emitter.onComplete()
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                        emitter.onError(Exception())
                    }
                }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

    private fun deleteReadings(): Completable {
        return Completable.create { emitter ->
            mReferenceReadings
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        for (document in task.result!!) {
                            document.reference.delete()
                            Log.d(TAG, document.id + " => " + document.data)
                        }
                        emitter.onComplete()
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                        emitter.onError(Exception())
                    }
                }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

    private fun backupAllReadings(readingEntities: List<ReadingEntity>): Completable {
        return Completable.create { emitter ->
            for (reading in readingEntities) {
                mReferenceReadings
                    .add(reading)
                    .addOnSuccessListener { documentReference ->
                        Log.d(
                            TAG,
                            "DocumentSnapshot added with ID: " + documentReference.id
                        )
                    }
                    .addOnFailureListener { e -> Log.d(TAG, "Error adding document", e) }
            }
            emitter.onComplete()
        }
    }

    private fun backupAllHomes(homeEntities: List<HomeEntity>): Completable {
        return Completable.create { emitter ->
            for (home in homeEntities) {
                mReferenceHomes
                    .add(home)
                    .addOnSuccessListener { documentReference ->
                        Log.d(
                            TAG,
                            "DocumentSnapshot added with ID: " + documentReference.id
                        )
                    }
                    .addOnFailureListener { e -> Log.d(TAG, "Error adding document", e) }
            }
            emitter.onComplete()
        }
    }

    fun restoreDb() {
        // 1. Очистить в БД таблицу с именем "reading"
        // 2. Очистить в БД таблицу с именем "home"
        // 3. Обновить таблицу с именем "home"
        // 4. Обновить таблицу с именем "reading"
        val subscribe = mDao.clearTableReadingRx()
            .andThen(mDao.clearTableHomeRx())
            .andThen(loadHomes())
            .flatMapCompletable { homeEntities ->
                mDao.insertInTableHomeRx(homeEntities).subscribeOn(Schedulers.io())
            }
            .andThen(loadReadings())
            .flatMapCompletable { readingEntities ->
                mDao.insertInTableReadingRx(readingEntities)
                    .subscribeOn(Schedulers.io())
            }
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                mIsRefreshing.postValue(true)
                mIsAvailable.postValue(false)
            }
            .doFinally {
                mIsRefreshing.postValue(false)
                mIsAvailable.postValue(true)
            }
            .doOnComplete {
                mMessageService.showMessage(R.string.successful_restoration)
            }
            .doOnError { throwable ->
                mMessageService.showMessage(R.string.unsuccessful_restoration)
                Log.d(TAG, "doOnError: $throwable")
            }
            .subscribe()
        mDisposables.add(subscribe)
    }

    private fun loadHomes(): Single<List<HomeEntity>> {
        return Single.create { emitter ->
            mReferenceHomes
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val homeEntities: MutableList<HomeEntity> = ArrayList()
                        for (document in task.result!!) {
                            val home = document.toObject(HomeEntity::class.java)
                            homeEntities.add(home)
                            Log.d(TAG, document.id + " => " + document.data)
                        }
                        emitter.onSuccess(homeEntities)
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                        emitter.onError(Exception())
                    }
                }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

    private fun loadReadings(): Single<List<ReadingEntity>> {
        return Single.create { emitter ->
            mReferenceReadings
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val readingEntities: MutableList<ReadingEntity> = ArrayList()
                        for (document in task.result!!) {
                            val reading = document.toObject(
                                ReadingEntity::class.java
                            )
                            readingEntities.add(reading)
                            Log.d(TAG, document.id + " => " + document.data)
                        }
                        emitter.onSuccess(readingEntities)
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                        emitter.onError(Exception())
                    }
                }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

    companion object {
        private const val TAG = "BackupCopyingViewModel"
    }

}