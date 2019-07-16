package com.github.sewerina.meter_readings.ui.chart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChartViewModel extends ViewModel {
    private final MutableLiveData<List<ReadingEntity>> mReadingEntities = new MutableLiveData<>();
    private AppDao mDao;
    private final Executor mExecutor;

    public ChartViewModel() {
        mDao = ReadingApp.mReadingDao;
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<ReadingEntity>> getReadings() {
        return mReadingEntities;
    }

    public void loadInChart(final HomeEntity currentHomeEntity) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<ReadingEntity> readingsForHome = mDao.getReadingsForHome(currentHomeEntity.id);
                mReadingEntities.postValue(readingsForHome);
            }
        });
    }
}
