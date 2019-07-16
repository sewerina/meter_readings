package com.github.sewerina.meter_readings.ui.homes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class HomesViewModel extends ViewModel {
    private AppDao mDao;


    public HomesViewModel() {
        mDao = ReadingApp.mReadingDao;
    }

    public LiveData<List<HomeEntity>> getHomes() {
        return mDao.getHomesLiveData();
    }

    public void addHome(final HomeEntity homeEntity) {
        mDao.insertHomeRx(homeEntity).subscribeOn(Schedulers.io()).subscribe();
    }

    public void deleteHome(final HomeEntity entity) {
        mDao.deleteHomeRx(entity).subscribeOn(Schedulers.io()).subscribe();
    }

    public void updateHome(final HomeEntity entity) {
        mDao.updateHomeRx(entity).subscribeOn(Schedulers.io()).subscribe();
    }

}
