package com.github.sewerina.meter_readings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_PREF_COLDWATER = "coldWater";
    private static final String KEY_PREF_HOTWATER = "hotWater";
    private static final String KEY_PREF_DRAINWATER = "drainWater";
    private static final String KEY_PREF_ELECTRICITY = "electricity";
    private static final String KEY_PREF_GAS = "gas";
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddReadingFaB;
    private ReadingAdapter mAdapter;
    private MainViewModel mViewModel;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getReadings().observe(this, new Observer<List<ReadingEntity>>() {
            @Override
            public void onChanged(List<ReadingEntity> readingEntities) {
                mAdapter.update(readingEntities);
            }
        });

        mAdapter = new ReadingAdapter();
        mRecyclerView = findViewById(R.id.recyclerReadings);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        mAddReadingFaB = findViewById(R.id.fab_addReading);
        mAddReadingFaB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewReadingDialog.showDialog(getSupportFragmentManager());
            }
        });

        mViewModel.load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_chart) {
            startActivity(new Intent(this, ChartActivity.class));
            return true;
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

        private LinearLayout mColdWaterLl;
        private LinearLayout mHotWaterLl;
        private LinearLayout mDrainWaterLl;
        private LinearLayout mElectricityLl;
        private LinearLayout mGasLl;

        private ReadingEntity mReadingEntity;

        public ReadingHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mColdWaterLl = itemView.findViewById(R.id.ll_coldWater);
            mHotWaterLl = itemView.findViewById(R.id.ll_hotWater);
            mDrainWaterLl = itemView.findViewById(R.id.ll_drainWater);
            mElectricityLl = itemView.findViewById(R.id.ll_electricity);
            mGasLl = itemView.findViewById(R.id.ll_gas);

            mDateTv = itemView.findViewById(R.id.tv_date);
            mColdWaterTv = itemView.findViewById(R.id.tv_coldWater);
            mHotWaterTv = itemView.findViewById(R.id.tv_hotWater);
            mDrainWaterTv = itemView.findViewById(R.id.tv_drainWater);
            mElectricityTv = itemView.findViewById(R.id.tv_electricity);
            mGasTv = itemView.findViewById(R.id.tv_gas);

            boolean isColdWater = mSharedPreferences.getBoolean(KEY_PREF_COLDWATER, true);
            boolean isHotWater = mSharedPreferences.getBoolean(KEY_PREF_HOTWATER, true);
            boolean isDrainWater = mSharedPreferences.getBoolean(KEY_PREF_DRAINWATER, true);
            boolean isElectricity = mSharedPreferences.getBoolean(KEY_PREF_ELECTRICITY, true);
            boolean isGas = mSharedPreferences.getBoolean(KEY_PREF_GAS, true);

            mColdWaterLl.setVisibility(isColdWater ? View.VISIBLE : View.GONE);
            mHotWaterLl.setVisibility(isHotWater ? View.VISIBLE : View.GONE);
            mDrainWaterLl.setVisibility(isDrainWater ? View.VISIBLE : View.GONE);
            mElectricityLl.setVisibility(isElectricity ? View.VISIBLE : View.GONE);
            mGasLl.setVisibility(isGas ? View.VISIBLE : View.GONE);
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
