package com.github.sewerina.meter_readings.ui.homes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomesViewModel extends ViewModel {
    private AppDao mDao;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public HomesViewModel() {
        mDao = ReadingApp.mReadingDao;
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
        Disposable subscribe = mDao.insertHomeRx(homeEntity).subscribeOn(Schedulers.io()).subscribe();
        mDisposables.add(subscribe);
    }

    public void deleteHome(final HomeEntity entity) {
        Disposable subscribe = mDao.deleteHomeRx(entity).subscribeOn(Schedulers.io()).subscribe();
        mDisposables.add(subscribe);
    }

    public void updateHome(final HomeEntity entity) {
        Disposable subscribe = mDao.updateHomeRx(entity).subscribeOn(Schedulers.io()).subscribe();
        mDisposables.add(subscribe);
    }

}
