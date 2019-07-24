package com.github.sewerina.meter_readings.ui.backup_copying;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.github.sewerina.meter_readings.ui.SingleLiveEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BackupCopyingViewModel extends ViewModel {
    private static final String TAG = "BackupCopyingViewModel";
    private final AppDao mDao;
    private final FirebaseFirestore mCloudFirestoreDb;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private MutableLiveData<Boolean> mIsRefreshing = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsAvailable = new MutableLiveData<>();

    private SingleLiveEvent<String> mMessage = new SingleLiveEvent<>();

    public BackupCopyingViewModel() {
        mDao = ReadingApp.mReadingDao;
        mCloudFirestoreDb = FirebaseFirestore.getInstance();
        mIsRefreshing.postValue(false);
        mIsAvailable.postValue(true);
        mMessage.postValue("");
    }

    public LiveData<Boolean> getIsRefreshing() {
        return mIsRefreshing;
    }

    public LiveData<Boolean> getIsAvailable() {
        return mIsAvailable;
    }

    public LiveData<String> getMessage() {
        return mMessage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposables.dispose();
    }

    public void backup() {
        // 1. Удалить данные из коллекции "homes" в CloudFirestore
        // 2. Удалить данные из коллекции "readings" в CloudFirestore
        // 3. Получить все readings из БД
        // 4. Отправить все readings в CloudFirestore
        // 5. Получить все homes из БД
        // 6. Отправить все homes в CloudFirestore
        Disposable subscribe = deleteHomes()
                .subscribeOn(Schedulers.io())
                .andThen(deleteReadings())
                .andThen(mDao.getAllReadingsRx().subscribeOn(Schedulers.io()))
                .flatMapCompletable(new Function<List<ReadingEntity>, CompletableSource>() {
                    @Override
                    public CompletableSource apply(List<ReadingEntity> readingEntities) throws Exception {
                        return backupAllReadings(readingEntities);
                    }
                }).andThen(mDao.getHomesRx().subscribeOn(Schedulers.io()))
                .flatMapCompletable(new Function<List<HomeEntity>, CompletableSource>() {
                    @Override
                    public CompletableSource apply(List<HomeEntity> homeEntities) throws Exception {
                        return backupAllHomes(homeEntities);
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mIsRefreshing.postValue(true);
                        mIsAvailable.postValue(false);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mIsRefreshing.postValue(false);
                        mIsAvailable.postValue(true);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        mMessage.postValue("Резервное копирование выполнилось успешно!");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mMessage.postValue("Не удалось выполнить резервное копирование!");
                    }
                })
                .subscribe();
        mDisposables.add(subscribe);
    }

    private Completable deleteHomes() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                mCloudFirestoreDb.collection("homes")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                    emitter.onComplete();
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    emitter.onError(new Exception());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        });
            }
        });
    }

    private Completable deleteReadings() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                mCloudFirestoreDb.collection("readings")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                    emitter.onComplete();
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    emitter.onError(new Exception());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        });
            }
        });
    }

    private Completable backupAllReadings(final List<ReadingEntity> readingEntities) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                CollectionReference collectionReference = mCloudFirestoreDb.collection("readings");
                for (ReadingEntity reading : readingEntities) {
                    collectionReference
                            .add(reading)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error adding document", e);
                                }
                            });
                }
                emitter.onComplete();
            }
        });
    }

    private Completable backupAllHomes(final List<HomeEntity> homeEntities) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                CollectionReference collectionReference = mCloudFirestoreDb.collection("homes");
                for (HomeEntity home : homeEntities) {
                    collectionReference
                            .add(home)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error adding document", e);
                                }
                            });
                }
                emitter.onComplete();
            }
        });
    }

    public void restoreDb() {
        // 1. Очистить в БД таблицу с именем "reading"
        // 2. Очистить в БД таблицу с именем "home"
        // 3. Обновить таблицу с именем "home"
        // 4. Обновить таблицу с именем "reading"
        Disposable subscribe = mDao.clearTableReadingRx()
                .andThen(mDao.clearTableHomeRx())
                .andThen(loadHomes())
                .flatMapCompletable(new Function<List<HomeEntity>, CompletableSource>() {
                    @Override
                    public CompletableSource apply(List<HomeEntity> homeEntities) throws Exception {
                        return mDao.insertInTableHomeRx(homeEntities).subscribeOn(Schedulers.io());
                    }
                })
                .andThen(loadReadings())
                .flatMapCompletable(new Function<List<ReadingEntity>, CompletableSource>() {
                    @Override
                    public CompletableSource apply(List<ReadingEntity> readingEntities) throws Exception {
                        return mDao.insertInTableReadingRx(readingEntities).subscribeOn(Schedulers.io());
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mIsRefreshing.postValue(true);
                        mIsAvailable.postValue(false);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mIsRefreshing.postValue(false);
                        mIsAvailable.postValue(true);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        mMessage.postValue("Обновление данных произошло успешно!");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mMessage.postValue("Не удалось обновить данные!");
                        Log.d(TAG, "doOnError: " + throwable);
                    }
                })
                .subscribe();
        mDisposables.add(subscribe);
    }

    private Single<List<HomeEntity>> loadHomes() {
        return Single.create(new SingleOnSubscribe<List<HomeEntity>>() {
            @Override
            public void subscribe(final SingleEmitter<List<HomeEntity>> emitter) throws Exception {
                mCloudFirestoreDb.collection("homes")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    final List<HomeEntity> homeEntities = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        HomeEntity home = document.toObject(HomeEntity.class);
                                        homeEntities.add(home);
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                    emitter.onSuccess(homeEntities);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    emitter.onError(new Exception());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        });
            }
        });
    }

    private Single<List<ReadingEntity>> loadReadings() {
        return Single.create(new SingleOnSubscribe<List<ReadingEntity>>() {
            @Override
            public void subscribe(final SingleEmitter<List<ReadingEntity>> emitter) throws Exception {
                mCloudFirestoreDb.collection("readings")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    final List<ReadingEntity> readingEntities = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ReadingEntity reading = document.toObject(ReadingEntity.class);
                                        readingEntities.add(reading);
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                    emitter.onSuccess(readingEntities);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    emitter.onError(new Exception());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        });
            }
        });
    }



}
