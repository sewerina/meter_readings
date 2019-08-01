package com.github.sewerina.meter_readings.ui.homes;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.CompletableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HomesViewModel extends ViewModel {
    private static final String TAG = "HomesViewModel";
    private final CompositeDisposable mDisposables = new CompositeDisposable();
    //    private final CollectionReference mCollectionReference;
    @Inject
    @Named("homes")
    CollectionReference mCollectionReference;

//    private AppDao mDao;
    @Inject
    AppDao mDao;

    public HomesViewModel() {
//        mDao = ReadingApp.sReadingDao;
//        FirebaseFirestore cloudFirestoreDb = FirebaseFirestore.getInstance();
//        mCollectionReference = cloudFirestoreDb.collection("homes");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposables.dispose();
    }

    public LiveData<List<HomeEntity>> getHomes() {
        return mDao.getHomesLiveData();
    }

    public void addHome(final HomeEntity homeEntity) {
        Disposable subscribe = mDao.insertHomeRx(homeEntity)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(new Function<Long, CompletableSource>() {
                    @Override
                    public CompletableSource apply(Long homeId) throws Exception {
                        homeEntity.id = homeId.intValue();
                        return addHomeInCloudFirestore(homeEntity);
                    }
                })
                .subscribe();
        mDisposables.add(subscribe);
    }

    private Completable addHomeInCloudFirestore(final HomeEntity homeEntity) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                mCollectionReference
                        .add(homeEntity)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                emitter.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Error adding document", e);
                                emitter.onError(new Exception());
                            }
                        });
            }
        });
    }

    public void deleteHome(final HomeEntity homeEntity) {
        Disposable subscribe = mDao.deleteHomeRx(homeEntity)
                .subscribeOn(Schedulers.io())
                .andThen(deleteHomeFromCloudFirestore(homeEntity))
                .subscribe();
        mDisposables.add(subscribe);
    }

    private Completable deleteHomeFromCloudFirestore(final HomeEntity homeEntity) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                mCollectionReference
                        .whereEqualTo("id", homeEntity.id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                    }
                                    emitter.onComplete();
                                } else {
                                    emitter.onError(new Exception());
                                }
                            }
                        });
            }
        });
    }

    public void updateHome(final HomeEntity homeEntity) {
        Disposable subscribe = mDao.updateHomeRx(homeEntity)
                .subscribeOn(Schedulers.io())
                .andThen(updateHomeInCloudFirestore(homeEntity))
                .subscribe();
        mDisposables.add(subscribe);
    }

    private Completable updateHomeInCloudFirestore(final HomeEntity homeEntity) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                mCollectionReference
                        .whereEqualTo("id", homeEntity.id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().set(homeEntity);
                                    }
                                    emitter.onComplete();
                                } else {
                                    emitter.onError(new Exception());
                                }
                            }
                        });
            }
        });
    }

}
