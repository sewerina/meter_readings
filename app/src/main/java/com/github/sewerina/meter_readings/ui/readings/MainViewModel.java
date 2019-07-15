package com.github.sewerina.meter_readings.ui.readings;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.AppDao;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;

import java.util.List;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<ReadingEntity>> mReadingEntities = new MutableLiveData<>();
    private final MutableLiveData<HomeEntity> mCurrentHome = new MutableLiveData<>();

    private final MutableLiveData<State> mState = new MutableLiveData<>();
    private HomeEntity mPreviousCurrentHome;
    private AppDao mDao;

    public MainViewModel() {
        mDao = ReadingApp.mReadingDao;
        Log.d("MainViewModel", "MainViewModel was created");
    }

    public LiveData<List<ReadingEntity>> getReadings() {
        return mReadingEntities;
    }

    public LiveData<State> getState() {
        return mState;
    }

    public void load() {
        List<HomeEntity> homes = mDao.getHomes();
        State state = new State(homes);
        if (mState.getValue() != null) {
            state.restore(mState.getValue().currentHomeEntity);
        } else if (mPreviousCurrentHome != null) {
            state.restore(mPreviousCurrentHome);
        } else {
            state.init();
        }
        mState.postValue(state);
    }

    public void addReading(ReadingEntity entity) {
        if (mCurrentHome.getValue() != null) {
            entity.homeId = mCurrentHome.getValue().id;
            mDao.insertReading(entity);
            loadReadings(mCurrentHome.getValue());
        }
    }

    public void deleteReading(ReadingEntity entity) {
        mDao.deleteReading(entity);
        if (mCurrentHome.getValue() != null) {
            loadReadings(mCurrentHome.getValue());
        }
    }

    public void updateReading(ReadingEntity entity) {
        mDao.updateReading(entity);
        if (mCurrentHome.getValue() != null) {
            loadReadings(mCurrentHome.getValue());
        }
    }

    private void loadReadings(HomeEntity homeEntity) {
        mReadingEntities.postValue(mDao.getReadingsForHome(homeEntity.id));
    }

    public void changeCurrentHome(int homePosition) {
        List<HomeEntity> homes = mDao.getHomes();
        if (homes != null) {
            HomeEntity entity = homes.get(homePosition);
            mCurrentHome.postValue(entity);
            loadReadings(entity);
        }

        if (mState.getValue() != null) {
            HomeEntity entity = mState.getValue().homeEntityList.get(homePosition);
            mState.getValue().currentHomePosition = homePosition;
            mState.getValue().currentHomeEntity = entity;
        }
    }

    public void restore(HomeEntity homeEntity) {
        mPreviousCurrentHome = homeEntity;
    }

    public class State {
        public final List<HomeEntity> homeEntityList;
        public HomeEntity currentHomeEntity;
        public int currentHomePosition = -1;

        public State(List<HomeEntity> homeEntityList) {
            this.homeEntityList = homeEntityList;
        }

        public void init() {
            currentHomePosition = 0;

            if (!homeEntityList.isEmpty()) {
                currentHomeEntity = homeEntityList.get(0);
            }

        }

        public void restore(HomeEntity previousHomeEntity) {
            for (int i = 0; i < homeEntityList.size(); i++) {
                HomeEntity homeEntity = homeEntityList.get(i);
                if (homeEntity.id == previousHomeEntity.id) {
                    currentHomeEntity = homeEntity;
                    currentHomePosition = i;
                    return;
                }
            }
            init();
        }
    }
}
