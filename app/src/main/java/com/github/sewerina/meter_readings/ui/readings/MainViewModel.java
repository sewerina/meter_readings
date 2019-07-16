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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<ReadingEntity>> mReadingEntities = new MutableLiveData<>();

    private final MutableLiveData<State> mState = new MutableLiveData<>();
    private HomeEntity mPreviousCurrentHome;
    private AppDao mDao;
    private final Executor mExecutor;

    public MainViewModel() {
        mDao = ReadingApp.mReadingDao;
        mExecutor = Executors.newSingleThreadExecutor();
        Log.d("MainViewModel", "MainViewModel was created");
    }

    public LiveData<List<ReadingEntity>> getReadings() {
        return mReadingEntities;
    }

    public LiveData<State> getState() {
        return mState;
    }

    public void load() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
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
                loadReadings(state.currentHomeEntity);
            }
        });
    }

    public void addReading(final ReadingEntity entity) {
        if (mState.getValue() != null) {
            entity.homeId = mState.getValue().currentHomeEntity.id;
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mDao.insertReading(entity);
                    loadReadings(mState.getValue().currentHomeEntity);
                }
            });
        }
    }

    public void deleteReading(final ReadingEntity entity) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDao.deleteReading(entity);
                if (mState.getValue() != null) {
                    loadReadings(mState.getValue().currentHomeEntity);
                }
            }
        });
    }

    public void updateReading(final ReadingEntity entity) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDao.updateReading(entity);
                if (mState.getValue() != null) {
                    loadReadings(mState.getValue().currentHomeEntity);
                }
            }
        });
    }

    private void loadReadings(final HomeEntity homeEntity) {
        mReadingEntities.postValue(mDao.getReadingsForHome(homeEntity.id));
    }

    public void changeCurrentHome(int homePosition) {
        if (mState.getValue() != null) {
            final HomeEntity homeEntity = mState.getValue().homeEntityList.get(homePosition);
            mState.getValue().currentHomePosition = homePosition;
            mState.getValue().currentHomeEntity = homeEntity;
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    loadReadings(homeEntity);
                }
            });
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
