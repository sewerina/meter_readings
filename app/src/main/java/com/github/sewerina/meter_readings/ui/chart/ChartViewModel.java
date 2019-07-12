package com.github.sewerina.meter_readings.ui.chart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;

import java.util.List;

public class ChartViewModel extends ViewModel {
    private final MutableLiveData<List<ReadingEntity>> mReadingEntities = new MutableLiveData<>();
    private AppDao mDao;

    public ChartViewModel() {
        mDao = ReadingApp.mReadingDao;
    }

    public LiveData<List<ReadingEntity>> getReadings() {
        return mReadingEntities;
    }

    public void loadInChart(HomeEntity currentHomeEntity) {
        List<ReadingEntity> readingsForHome = mDao.getReadingsForHome(currentHomeEntity.id);
        mReadingEntities.postValue(readingsForHome);
    }
}
