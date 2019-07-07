package com.github.sewerina.meter_readings;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<ReadingEntity>> mReadingEntities = new MutableLiveData<>();
    private final MutableLiveData<List<HomeEntity>> mHomeEntities = new MutableLiveData<>();
    private ReadingDao mReadingDao;
    private final MutableLiveData<HomeEntity> mCurrentHome = new MutableLiveData<>();

    public MainViewModel() {
        mReadingDao = ReadingApp.mReadingDao;
        Log.d("MainViewModel", "MainViewModel was created");
    }

    public LiveData<List<ReadingEntity>> getReadings() {
        return mReadingEntities;
    }

    public LiveData<List<HomeEntity>> getHomes() {
        return mHomeEntities;
    }

    public LiveData<HomeEntity> getCurrentHome() {
        return mCurrentHome;
    }

    public void addReading(ReadingEntity entity) {
//        List<ReadingEntity> entities = mReadingEntities.getValue();
//
//        if (entities == null) {
//            entities = new ArrayList<>();
//        }
//        entities.add(entity);
//        mReadingEntities.postValue(entities);

        if (mCurrentHome.getValue() != null) {
            entity.homeId = mCurrentHome.getValue().id;
            mReadingDao.insert(entity);
            loadReadings(mCurrentHome.getValue());
        }
    }

    public void firstLoad() {
        HomeEntity homeEntity = mReadingDao.getHomes().get(0);
        mCurrentHome.postValue(homeEntity);
        loadReadings(homeEntity);
    }

    private void loadReadings(HomeEntity homeEntity) {
        mReadingEntities.postValue(mReadingDao.getReadingsForHome(homeEntity.id));
    }


    public void deleteReading(ReadingEntity entity) {
        // NEED ReadingEntity
        mReadingDao.delete(entity);
        if (mCurrentHome.getValue() != null) {
            loadReadings(mCurrentHome.getValue());
        }
    }

    public void updateReading(ReadingEntity entity) {
        mReadingDao.update(entity);
        if (mCurrentHome.getValue() != null) {
            loadReadings(mCurrentHome.getValue());
        }
    }

    public void changeCurrentHome(int homePosition) {
        List<HomeEntity> homes = mHomeEntities.getValue();
        if (homes != null) {
            HomeEntity entity = homes.get(homePosition);
            mCurrentHome.postValue(entity);
            loadReadings(entity);
        }
    }

    public void loadHomes() {
        mHomeEntities.postValue(mReadingDao.getHomes());
    }

}
