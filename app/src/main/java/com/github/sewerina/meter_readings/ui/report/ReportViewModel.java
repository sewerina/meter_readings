package com.github.sewerina.meter_readings.ui.report;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

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
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ReportViewModel extends ViewModel {
    private final CompositeDisposable mDisposables = new CompositeDisposable();
    private MutableLiveData<List<Report>> mReports = new MutableLiveData<>();

    @Inject
    AppDao mDao;

    @Inject
    @Named("reports")
    CollectionReference mReportCollectionReference;

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

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposables.dispose();
    }
}
