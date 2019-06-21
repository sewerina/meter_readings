package com.github.sewerina.meter_readings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddReadingFaB;
    private ReadingAdapter mAdapter;
    private MainViewModel mViewModel;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        private ReadingEntity mReadingEntity;

        public ReadingHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mDateTv = itemView.findViewById(R.id.tv_date);
            mColdWaterTv = itemView.findViewById(R.id.tv_coldWater);
            mHotWaterTv = itemView.findViewById(R.id.tv_hotWater);
            mDrainWaterTv = itemView.findViewById(R.id.tv_drainWater);
            mElectricityTv = itemView.findViewById(R.id.tv_electricity);
            mGasTv = itemView.findViewById(R.id.tv_gas);
        }

        void bind(ReadingEntity entity) {
            mReadingEntity = entity;

            mDateTv.setText(entity.date.toString());
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
