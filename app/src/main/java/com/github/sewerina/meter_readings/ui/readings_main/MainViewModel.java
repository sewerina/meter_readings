package com.github.sewerina.meter_readings.ui.readings_main;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<ReadingEntity>> mReadingEntities = new MutableLiveData<>();
    private final MutableLiveData<State> mState = new MutableLiveData<>();
    private final CompositeDisposable mDisposables = new CompositeDisposable();
    private HomeEntity mPreviousCurrentHome;
    private AppDao mDao;
    private final CollectionReference mCollectionReference;

    public MainViewModel() {
        mDao = ReadingApp.mReadingDao;
        FirebaseFirestore cloudFirestoreDb = FirebaseFirestore.getInstance();
        mCollectionReference = cloudFirestoreDb.collection("readings");
        Log.d("MainViewModel", "MainViewModel was created");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposables.dispose();
    }

    public LiveData<List<ReadingEntity>> getReadings() {
        return mReadingEntities;
    }

    public LiveData<State> getState() {
        return mState;
    }

    public void load() {
        Disposable subscribe = mDao.getHomesRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<HomeEntity>, Single<List<ReadingEntity>>>() {
                    @Override
                    public Single<List<ReadingEntity>> apply(List<HomeEntity> homeEntities) throws Exception {
                        State state = new State(homeEntities);
                        if (mState.getValue() != null) {
                            state.restore(mState.getValue().currentHomeEntity);
                        } else if (mPreviousCurrentHome != null) {
                            state.restore(mPreviousCurrentHome);
                        } else {
                            state.init();
                        }
                        mState.postValue(state);
                        return loadReadingsRx(state.currentHomeEntity);
                    }
                })
                .subscribe();
        mDisposables.add(subscribe);
    }

    public void addReading(final ReadingEntity readingEntity) {
        if (mState.getValue() != null) {
            HomeEntity currentHomeEntity = mState.getValue().currentHomeEntity;
            readingEntity.homeId = currentHomeEntity.id;
            Disposable subscribe = mDao.insertReadingRx(readingEntity)
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable(new Function<Long, CompletableSource>() {
                        @Override
                        public CompletableSource apply(Long readingId) throws Exception {
                            readingEntity.id = readingId.intValue();
                            return addReadingInCloudFirestore(readingEntity);
                        }
                    })
                    .andThen(loadReadingsRx(currentHomeEntity))
                    .subscribe();
            mDisposables.add(subscribe);
        }
    }

    private Completable addReadingInCloudFirestore(final ReadingEntity readingEntity) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                mCollectionReference
                        .add(readingEntity)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                emitter.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(new Exception());
                            }
                        });
            }
        });
    }

    public void deleteReading(final ReadingEntity entity) {
        if (mState.getValue() != null) {
            HomeEntity currentHomeEntity = mState.getValue().currentHomeEntity;
            Disposable subscribe = mDao.deleteReadingRx(entity)
                    .subscribeOn(Schedulers.io())
                    .andThen(loadReadingsRx(currentHomeEntity))
                    .subscribe();
            mDisposables.add(subscribe);
        }
    }

    public void updateReading(final ReadingEntity entity) {
        if (mState.getValue() != null) {
            HomeEntity currentHomeEntity = mState.getValue().currentHomeEntity;
            Disposable subscribe = mDao.updateReadingRx(entity)
                    .subscribeOn(Schedulers.io())
                    .andThen(loadReadingsRx(currentHomeEntity))
                    .subscribe();
            mDisposables.add(subscribe);
        }
    }

    private Single<List<ReadingEntity>> loadReadingsRx(final HomeEntity homeEntity) {
        if (homeEntity == null) {
            return Single.just((List<ReadingEntity>)new ArrayList<ReadingEntity>());
        }
        return mDao.getReadingsForHomeRx(homeEntity.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Consumer<List<ReadingEntity>>() {
                    @Override
                    public void accept(List<ReadingEntity> readingEntities) throws Exception {
                        mReadingEntities.postValue(readingEntities);
                    }
                });
    }

    public void changeCurrentHome(int homePosition) {
        if (mState.getValue() != null) {
            final HomeEntity homeEntity = mState.getValue().homeEntityList.get(homePosition);
            mState.getValue().currentHomePosition = homePosition;
            mState.getValue().currentHomeEntity = homeEntity;
            Disposable subscribe = loadReadingsRx(homeEntity).subscribe();
            mDisposables.add(subscribe);
        }
    }

    public void restore(HomeEntity homeEntity) {
        mPreviousCurrentHome = homeEntity;
    }

    public class State {
        public final List<HomeEntity> homeEntityList;
        public HomeEntity currentHomeEntity;
        public int currentHomePosition = -1;

        public State(List<HomeEntity> homeEntityList) {
            this.homeEntityList = homeEntityList;
        }

        public void init() {
            currentHomePosition = 0;
            if (!homeEntityList.isEmpty()) {
                currentHomeEntity = homeEntityList.get(0);
            }
        }

        public void restore(HomeEntity previousHomeEntity) {
            for (int i = 0; i < homeEntityList.size(); i++) {
                HomeEntity homeEntity = homeEntityList.get(i);
                if (homeEntity.id == previousHomeEntity.id) {
                    currentHomeEntity = homeEntity;
                    currentHomePosition = i;
                    return;
                }
            }
            init();
        }
    }
}
