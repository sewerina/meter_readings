package com.github.sewerina.meter_readings;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<ReadingEntity>> mReadingEntities = new MutableLiveData<>();

    public MainViewModel() {
        Log.d("MainViewModel", "MainViewModel was created");
    }

    public LiveData<List<ReadingEntity>> getReadings() {
        return mReadingEntities;
    }

    public void addReading(ReadingEntity entity) {
        List<ReadingEntity> entities = mReadingEntities.getValue();

        if (entities == null) {
            entities = new ArrayList<>();
        }
        entities.add(entity);
        mReadingEntities.postValue(entities);
    }


}
