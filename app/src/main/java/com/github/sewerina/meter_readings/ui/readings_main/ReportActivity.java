package com.github.sewerina.meter_readings.ui.readings_main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.github.sewerina.meter_readings.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReportActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddReportFab;
    private ReportViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mRecyclerView = findViewById(R.id.recyclerReports);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAddReportFab = findViewById(R.id.fab_addReport);
        mAddReportFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.addReport();
            }
        });

        mViewModel = ViewModelProviders.of(this).get(ReportViewModel.class);

    }
}
