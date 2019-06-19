package com.github.sewerina.meter_readings;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<ReadingEntity>> mReadingEntities = new MutableLiveData<>();
    private ReadingDao mReadingDao;

    public MainViewModel() {
        mReadingDao = ReadingApp.mReadingDao;
        Log.d("MainViewModel", "MainViewModel was created");
    }

    public LiveData<List<ReadingEntity>> getReadings() {
        return mReadingEntities;
    }

    public void addReading(ReadingEntity entity) {
//        List<ReadingEntity> entities = mReadingEntities.getValue();
//
//        if (entities == null) {
//            entities = new ArrayList<>();
//        }
//        entities.add(entity);
//        mReadingEntities.postValue(entities);
        mReadingDao.insert(entity);
        load();
    }

    public void load() {
        mReadingEntities.postValue(mReadingDao.getAll());
    }


    public void deleteReading(ReadingEntity entity) {
        // NEED ReadingEntity
        mReadingDao.delete(entity);
        load();
    }

    public void updateReading(ReadingEntity entity) {
        mReadingDao.update(entity);
        load();
    }
}
