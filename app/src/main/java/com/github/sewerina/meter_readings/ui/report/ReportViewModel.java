package com.github.sewerina.meter_readings.ui.report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ReportViewModel extends ViewModel {
    private final CompositeDisposable mDisposables = new CompositeDisposable();
    private MutableLiveData<List<Report>> mReports = new MutableLiveData<>();

    @Inject
    AppDao mDao;

    public LiveData<List<Report>> getReports() {
        return mReports;
    }

    public void addReport(HomeEntity currentHomeEntity) {
        Disposable subscribe = mDao.getReadingsForHomeRx(currentHomeEntity.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ReadingEntity>>() {
                    @Override
                    public void accept(List<ReadingEntity> readingEntities) throws Exception {
                        Report report = new Report(readingEntities);
                        List<Report> reportList = mReports.getValue();

                        if (reportList == null) {
                            reportList = new ArrayList<>();
                        }
                        reportList.add(report);
                        mReports.postValue(reportList);
                    }
                });
        mDisposables.add(subscribe);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposables.dispose();
    }
}
