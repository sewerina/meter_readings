package com.github.sewerina.meter_readings.ui.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.sewerina.meter_readings.FormattedDate;
import com.github.sewerina.meter_readings.R;
import com.github.sewerina.meter_readings.ReadingApp;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.github.sewerina.meter_readings.ui.MainComponent;
import com.github.sewerina.meter_readings.ui.chart.ChartActivity;
import com.github.sewerina.meter_readings.ui.readings_main.BottomSheetReadingDialog;
import com.github.sewerina.meter_readings.ui.readings_main.MainActivity;
import com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences.KEY_PREF_COLD_WATER;
import static com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences.KEY_PREF_DRAIN_WATER;
import static com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences.KEY_PREF_ELECTRICITY;
import static com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences.KEY_PREF_GAS;
import static com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences.KEY_PREF_HOT_WATER;

public class ReportActivity extends AppCompatActivity {
    private static final String EXTRA_CURRENT_HOME_ENTITY = "currentHomeEntity";
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddReportFab;
    private ReportViewModel mViewModel;
    private HomeEntity mCurrentHomeEntity;

    public static Intent newIntent(Context context, HomeEntity homeEntity) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra(EXTRA_CURRENT_HOME_ENTITY, homeEntity);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        if (getIntent() != null) {
            mCurrentHomeEntity  = (HomeEntity) getIntent().getSerializableExtra(EXTRA_CURRENT_HOME_ENTITY);
        }

        if (mCurrentHomeEntity != null && mCurrentHomeEntity.address != null) {
            setTitle("Отчеты для " + mCurrentHomeEntity.address);
        }

        mRecyclerView = findViewById(R.id.recyclerReports);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ReportAdapter reportAdapter = new ReportAdapter();
        mRecyclerView.setAdapter(reportAdapter);

        mAddReportFab = findViewById(R.id.fab_addReport);
        mAddReportFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.addReport(mCurrentHomeEntity);
            }
        });

        mViewModel = ViewModelProviders.of(this).get(ReportViewModel.class);
        ReadingApp.sMainComponent.inject(mViewModel);

        mViewModel.getReports().observe(this, new Observer<List<Report>>() {
            @Override
            public void onChanged(List<Report> reports) {
                reportAdapter.update(reports);
            }
        });
    }

    private class ReportHolder extends RecyclerView.ViewHolder {
        private TextView mMessageTv;

        private Report mReport;

        public ReportHolder(@NonNull View itemView) {
            super(itemView);
            mMessageTv = (TextView) itemView;
        }

        void bind(Report report) {
            mReport = report;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            boolean isColdWater = sharedPreferences.getBoolean(KEY_PREF_COLD_WATER, true);
            boolean isHotWater = sharedPreferences.getBoolean(KEY_PREF_HOT_WATER, true);
            boolean isDrainWater = sharedPreferences.getBoolean(KEY_PREF_DRAIN_WATER, true);
            boolean isElectricity = sharedPreferences.getBoolean(KEY_PREF_ELECTRICITY, true);
            boolean isGas = sharedPreferences.getBoolean(KEY_PREF_GAS, true);
            String message = mReport.reportMessage(isColdWater, isHotWater, isDrainWater, isElectricity, isGas, mCurrentHomeEntity.address);
            mMessageTv.setText(message);
        }

    }

    private class ReportAdapter extends RecyclerView.Adapter<ReportHolder> {
        private final List<Report> mReports = new ArrayList<>();

        @NonNull
        @Override
        public ReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            return new ReportHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull ReportHolder holder, int position) {
            holder.bind(mReports.get(position));
        }

        @Override
        public int getItemCount() {
            return mReports.size();
        }

        void update(List<Report> reports) {
            mReports.clear();
            mReports.addAll(reports);
            notifyDataSetChanged();
        }

        void add(Report report) {
            mReports.add(report);
            notifyItemInserted(mReports.size() - 1);
        }
    }
}
