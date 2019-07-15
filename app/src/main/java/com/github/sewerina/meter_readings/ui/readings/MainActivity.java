package com.github.sewerina.meter_readings.ui.readings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.sewerina.meter_readings.FormattedDate;
import com.github.sewerina.meter_readings.R;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.github.sewerina.meter_readings.notification.NotificationWorker;
import com.github.sewerina.meter_readings.notification.RemindNotification;
import com.github.sewerina.meter_readings.ui.chart.ChartActivity;
import com.github.sewerina.meter_readings.ui.homes.HomesActivity;
import com.github.sewerina.meter_readings.ui.settings.SettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String CURRENT_HOME_ENTITY = "currentHomeEntity";

    private Spinner mSpinner;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddReadingFab;

    private ArrayAdapter<String> mSpinnerAdapter;
    private ReadingAdapter mReadingAdapter;

    private MainViewModel mViewModel;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mViewModel.changeCurrentHome(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        if (savedInstanceState != null) {
            HomeEntity homeEntity = (HomeEntity) savedInstanceState.getSerializable(CURRENT_HOME_ENTITY);
            mViewModel.restore(homeEntity);
        }
        mViewModel.getReadings().observe(this, new Observer<List<ReadingEntity>>() {
            @Override
            public void onChanged(List<ReadingEntity> readingEntities) {
                mReadingAdapter.update(readingEntities);
            }
        });

        mViewModel.getState().observe(this, new Observer<MainViewModel.State>() {
            @Override
            public void onChanged(MainViewModel.State state) {
                mSpinnerAdapter.clear();
                List<HomeEntity> homeEntityList = state.homeEntityList;
                for (HomeEntity homeEntity : homeEntityList) {
                    mSpinnerAdapter.add(homeEntity.address);
                }
                mSpinnerAdapter.notifyDataSetChanged();
                mSpinner.setSelection(state.currentHomePosition);
            }
        });

        mSpinner = findViewById(R.id.spinner_home);
        mSpinnerAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_item, new ArrayList<String>());
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(mOnItemSelectedListener);

        mReadingAdapter = new ReadingAdapter();
        mRecyclerView = findViewById(R.id.recyclerReadings);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mReadingAdapter);

        mAddReadingFab = findViewById(R.id.fab_addReading);
        mAddReadingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewReadingDialog.showDialog(getSupportFragmentManager());
            }
        });

//        mViewModel.firstLoad();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (mViewModel.getState().getValue() != null) {
            outState.putSerializable(CURRENT_HOME_ENTITY, mViewModel.getState().getValue().currentHomeEntity);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.load();
//        new RemindNotification(this).appearNotification();
        PeriodicWorkRequest notificationWorkRequest = new PeriodicWorkRequest
                .Builder(NotificationWorker.class, 1, TimeUnit.HOURS)
                .build();
        WorkManager.getInstance(this).enqueue(notificationWorkRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_homes) {
            startActivity(new Intent(this, HomesActivity.class));
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_chart) {
            if (mViewModel.getState().getValue() != null) {
                startActivity(ChartActivity.newIntent(this, mViewModel.getState().getValue().currentHomeEntity));
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class ReadingHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDateTv;
        private TextView mColdWaterTv;
        private TextView mHotWaterTv;
        private TextView mDrainWaterTv;
        private TextView mElectricityTv;
        private TextView mGasTv;

        private ReadingEntity mReadingEntity;

        public ReadingHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            new ReadingPreferences(itemView.getContext()).setLayoutVisibility(itemView);

            mDateTv = itemView.findViewById(R.id.tv_date);
            mColdWaterTv = itemView.findViewById(R.id.tv_coldWater);
            mHotWaterTv = itemView.findViewById(R.id.tv_hotWater);
            mDrainWaterTv = itemView.findViewById(R.id.tv_drainWater);
            mElectricityTv = itemView.findViewById(R.id.tv_electricity);
            mGasTv = itemView.findViewById(R.id.tv_gas);
        }

        void bind(ReadingEntity entity) {
            mReadingEntity = entity;

            mDateTv.setText(new FormattedDate(entity.date).text());
            mColdWaterTv.setText(String.valueOf(entity.coldWater));
            mHotWaterTv.setText(String.valueOf(entity.hotWater));
            mDrainWaterTv.setText(String.valueOf(entity.drainWater));
            mElectricityTv.setText(String.valueOf(entity.electricity));
            mGasTv.setText(String.valueOf(entity.gas));
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: ");
            BottomSheetReadingDialog.showDialog(getSupportFragmentManager(), mReadingEntity);
        }
    }

    private class ReadingAdapter extends RecyclerView.Adapter<ReadingHolder> {
        private final List<ReadingEntity> mReadings = new ArrayList<>();

        @NonNull
        @Override
        public ReadingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reading_list_item, parent, false);
            return new ReadingHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ReadingHolder holder, int position) {
            holder.bind(mReadings.get(position));
        }

        @Override
        public int getItemCount() {
            return mReadings.size();
        }

        void update(List<ReadingEntity> entities) {
            mReadings.clear();
            mReadings.addAll(entities);
            notifyDataSetChanged();
        }

        void add(ReadingEntity entity) {
            mReadings.add(entity);
            notifyItemInserted(mReadings.size() - 1);
        }
    }


}
