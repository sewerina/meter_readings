package com.github.sewerina.meter_readings.ui.homes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomesViewModel extends ViewModel {
    private final MutableLiveData<List<HomeEntity>> mHomeEntities = new MutableLiveData<>();
    private final Executor mExecutor;
    private AppDao mDao;


    public HomesViewModel() {
        mDao = ReadingApp.mReadingDao;
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<HomeEntity>> getHomes() {
        return mHomeEntities;
    }

    // Asynchronous
    public void loadHomesAsync() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mHomeEntities.postValue(mDao.getHomes());
            }
        });
    }

    // Synchronous
    private void _loadHomes() {
        mHomeEntities.postValue(mDao.getHomes());
    }

    public void addHome(final HomeEntity homeEntity) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDao.insertHome(homeEntity);
                _loadHomes();
            }
        });
    }

    public void deleteHome(final HomeEntity entity) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDao.deleteHome(entity);
                _loadHomes();
            }
        });
    }

    public void updateHome(final HomeEntity entity) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDao.updateHome(entity);
                _loadHomes();
            }
        });
    }

}
