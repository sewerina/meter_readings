package com.github.sewerina.meter_readings.ui.readings_main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sewerina.meter_readings.database.AppDao
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.database.NewReadingEntity
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.google.firebase.firestore.CollectionReference
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

class MainViewModel @Inject constructor(
    var mDao: AppDao,

    @Named("readings")
    var mCollectionReference: CollectionReference
) : ViewModel() {
    private val mReadingEntities = MutableLiveData<List<ReadingEntity>>()
    private val mState = MutableLiveData<State?>()
    private val mDisposables = CompositeDisposable()

    private var mPreviousCurrentHome: HomeEntity? = null

    override fun onCleared() {
        super.onCleared()
        mDisposables.dispose()
    }

    val readings: LiveData<List<ReadingEntity>>
        get() = mReadingEntities
    val state: LiveData<State?>
        get() = mState

    fun load() {
        val subscribe = mDao.homesRx
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { homeEntities ->
                val state: State = State(homeEntities)
                if (mState.value != null) {
                    state.restore(mState.value!!.currentHomeEntity)
                } else if (mPreviousCurrentHome != null) {
                    state.restore(mPreviousCurrentHome)
                } else {
                    state.init()
                }
                mState.postValue(state)
                loadReadingsRx(state.currentHomeEntity)
            }
            .subscribe()
        mDisposables.add(subscribe)
    }

    fun addReading(readingEntity: NewReadingEntity) {
        if (mState.value != null) {
            val currentHomeEntity = mState.value!!.currentHomeEntity
            readingEntity.homeId = currentHomeEntity!!.id
            val subscribe = mDao.insertReadingRx(readingEntity)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable { readingId ->
                    addReadingInCloudFirestore(readingEntity.toEntity(readingId.toInt()))
                }
                .andThen(loadReadingsRx(currentHomeEntity))
                .subscribe()
            mDisposables.add(subscribe)
        }
    }

    private fun addReadingInCloudFirestore(readingEntity: ReadingEntity): Completable {
        return Completable.create { emitter ->
            mCollectionReference
                .add(readingEntity)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(Exception()) }
        }
    }

    fun deleteReading(readingEntity: ReadingEntity) {
        if (mState.value != null) {
            val currentHomeEntity = mState.value!!.currentHomeEntity
            val subscribe = mDao.deleteReadingRx(readingEntity)
                .subscribeOn(Schedulers.io())
                .andThen(deleteReadingFromCloudFirestore(readingEntity))
                .andThen(loadReadingsRx(currentHomeEntity))
                .subscribe()
            mDisposables.add(subscribe)
        }
    }

    private fun deleteReadingFromCloudFirestore(readingEntity: ReadingEntity): Completable {
        return Completable.create { emitter ->
            mCollectionReference
                .whereEqualTo("id", readingEntity.id)
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

    fun updateReading(readingEntity: ReadingEntity) {
        if (mState.value != null) {
            val currentHomeEntity = mState.value!!.currentHomeEntity
            val subscribe = mDao.updateReadingRx(readingEntity)
                .subscribeOn(Schedulers.io())
                .andThen(updateReadingInCloudFirestore(readingEntity))
                .andThen(loadReadingsRx(currentHomeEntity))
                .subscribe()
            mDisposables.add(subscribe)
        }
    }

    private fun updateReadingInCloudFirestore(readingEntity: ReadingEntity): Completable {
        return Completable.create { emitter ->
            mCollectionReference
                .whereEqualTo("id", readingEntity.id)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        for (document in task.result!!) {
                            document.reference.set(readingEntity)
                        }
                        emitter.onComplete()
                    } else {
                        emitter.onError(Exception())
                    }
                }
        }
    }

    private fun loadReadingsRx(homeEntity: HomeEntity?): Single<List<ReadingEntity>> {
        return if (homeEntity == null) {
            Single.just(ArrayList<ReadingEntity>() as List<ReadingEntity>)
        } else mDao.getReadingsForHomeRx(homeEntity.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { readingEntities -> mReadingEntities.postValue(readingEntities) }
    }

    fun changeCurrentHome(homePosition: Int) {
        if (mState.value != null) {
            val homeEntity = mState.value!!.homeEntityList[homePosition]
            mState.value!!.currentHomePosition = homePosition
            mState.value!!.currentHomeEntity = homeEntity
            val subscribe = loadReadingsRx(homeEntity).subscribe()
            mDisposables.add(subscribe)
        }
    }

    fun restore(homeEntity: HomeEntity?) {
        mPreviousCurrentHome = homeEntity
    }

    inner class State(val homeEntityList: List<HomeEntity>) {
        var currentHomeEntity: HomeEntity? = null
        var currentHomePosition = -1
        fun init() {
            currentHomePosition = 0
            if (homeEntityList.isNotEmpty()) {
                currentHomeEntity = homeEntityList[0]
            }
        }

        fun restore(previousHomeEntity: HomeEntity?) {
            for (i in homeEntityList.indices) {
                val homeEntity = homeEntityList[i]
                if (homeEntity.id == previousHomeEntity!!.id) {
                    currentHomeEntity = homeEntity
                    currentHomePosition = i
                    return
                }
            }
            init()
        }
    }
}