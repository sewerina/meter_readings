package com.github.sewerina.meter_readings.ui.homes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;

import java.util.List;

public class HomesViewModel extends ViewModel {
    private final MutableLiveData<List<HomeEntity>> mHomeEntities = new MutableLiveData<>();
    private AppDao mDao;

    public HomesViewModel() {
        mDao = ReadingApp.mReadingDao;
    }

    public LiveData<List<HomeEntity>> getHomes() {
        return mHomeEntities;
    }

    public void loadHomes() {
        mHomeEntities.postValue(mDao.getHomes());
    }

    public void addHome(HomeEntity homeEntity) {
        mDao.insertHome(homeEntity);
        loadHomes();
    }

    public void deleteHome(HomeEntity entity) {
        mDao.deleteHome(entity);
        loadHomes();
    }

    public void updateHome(HomeEntity entity) {
        mDao.updateHome(entity);
        loadHomes();
    }

}
