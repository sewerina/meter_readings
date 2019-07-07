package com.github.sewerina.meter_readings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomesActivity extends AppCompatActivity {
    private static final String TAG = "HomesActivity";
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homes);

        mRecyclerView = findViewById(R.id.recyclerHomes);

    }

    private class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private TextView mDateTv;
//        private TextView mColdWaterTv;
//        private TextView mHotWaterTv;
//        private TextView mDrainWaterTv;
//        private TextView mElectricityTv;
//        private TextView mGasTv;

        private HomeEntity mHomeEntity;

        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

//            new ReadingPreferences(itemView.getContext()).setLayoutVisibility(itemView);
//
//            mDateTv = itemView.findViewById(R.id.tv_date);
//            mColdWaterTv = itemView.findViewById(R.id.tv_coldWater);
//            mHotWaterTv = itemView.findViewById(R.id.tv_hotWater);
//            mDrainWaterTv = itemView.findViewById(R.id.tv_drainWater);
//            mElectricityTv = itemView.findViewById(R.id.tv_electricity);
//            mGasTv = itemView.findViewById(R.id.tv_gas);
        }

        void bind(HomeEntity entity) {
            mHomeEntity = entity;

//            mDateTv.setText(new FormattedDate(entity.date).text());
//            mColdWaterTv.setText(String.valueOf(entity.coldWater));
//            mHotWaterTv.setText(String.valueOf(entity.hotWater));
//            mDrainWaterTv.setText(String.valueOf(entity.drainWater));
//            mElectricityTv.setText(String.valueOf(entity.electricity));
//            mGasTv.setText(String.valueOf(entity.gas));
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: ");
//            BottomSheetReadingDialog.showDialog(getSupportFragmentManager(), mReadingEntity);
        }
    }

    private class HomeAdapter extends RecyclerView.Adapter<HomeHolder> {
        private final List<HomeEntity> mHomes = new ArrayList<>();

        @NonNull
        @Override
        public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false);
            return new HomeHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
            holder.bind(mHomes.get(position));
        }

        @Override
        public int getItemCount() {
            return mHomes.size();
        }

        void update(List<HomeEntity> entities) {
            mHomes.clear();
            mHomes.addAll(entities);
            notifyDataSetChanged();
        }

        void add(HomeEntity entity) {
            mHomes.add(entity);
            notifyItemInserted(mHomes.size() - 1);
        }
    }
}
