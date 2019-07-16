package com.github.sewerina.meter_readings.ui.homes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomesViewModel extends ViewModel {
    private final Executor mExecutor;
    private AppDao mDao;


    public HomesViewModel() {
        mDao = ReadingApp.mReadingDao;
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<HomeEntity>> getHomes() {
        return mDao.getHomesLiveData();
    }

    public void addHome(final HomeEntity homeEntity) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDao.insertHome(homeEntity);
            }
        });
    }

    public void deleteHome(final HomeEntity entity) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDao.deleteHome(entity);
            }
        });
    }

    public void updateHome(final HomeEntity entity) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDao.updateHome(entity);
            }
        });
    }

}
