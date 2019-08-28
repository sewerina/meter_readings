package com.github.sewerina.meter_readings.ui.report;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ReportViewModel extends ViewModel {
    private final CompositeDisposable mDisposables = new CompositeDisposable();
    @Inject
    AppDao mDao;
    @Inject
    @Named("reports")
    CollectionReference mReportCollectionReference;
    private MutableLiveData<List<Report>> mReports = new MutableLiveData<>();

    public LiveData<List<Report>> getReports() {
        return mReports;
    }

    public void addReport(HomeEntity currentHomeEntity) {
        Disposable subscribe = mDao.getReadingsForHomeRx(currentHomeEntity.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<ReadingEntity>, SingleSource<Report>>() {
                    @Override
                    public SingleSource<Report> apply(final List<ReadingEntity> readingEntities) throws Exception {
                        return Single.fromCallable(new Callable<Report>() {
                            @Override
                            public Report call() throws Exception {
                                Report report = new Report(readingEntities);
                                List<Report> reportList = mReports.getValue();

                                if (reportList == null) {
                                    reportList = new ArrayList<>();
                                }
                                reportList.add(report);
                                mReports.postValue(reportList);

                                return report;
                            }
                        });
                    }
                })
                .flatMapCompletable(new Function<Report, CompletableSource>() {
                    @Override
                    public CompletableSource apply(Report report) throws Exception {
                        return addReportInCloudFirestore(report);
                    }
                })
                .subscribe();
        mDisposables.add(subscribe);
    }

    private Completable addReportInCloudFirestore(final Report report) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                mReportCollectionReference
                        .add(report)
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

    public void loadReports(HomeEntity currentHome) {
        // 1. Пойти в Firebase & взять оттуда все reports для currentHome.Id
        // 2. Обновить этими reports MutableLiveData
        Disposable subscribe = loadReportsFromCloudFirestore(currentHome.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Consumer<List<Report>>() {
                    @Override
                    public void accept(List<Report> reports) throws Exception {
                        mReports.postValue(reports);
                    }
                })
                .subscribe();
        mDisposables.add(subscribe);
    }

    private Single<List<Report>> loadReportsFromCloudFirestore(final int currentHomeId) {
        return Single.create(new SingleOnSubscribe<List<Report>>() {
            @Override
            public void subscribe(final SingleEmitter<List<Report>> emitter) throws Exception {
                mReportCollectionReference
                        .whereEqualTo("homeId", currentHomeId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    List<Report> reports = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Report report = document.toObject(Report.class);
                                        reports.add(report);
                                    }
                                    emitter.onSuccess(reports);
                                } else {
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

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposables.dispose();
    }

}
