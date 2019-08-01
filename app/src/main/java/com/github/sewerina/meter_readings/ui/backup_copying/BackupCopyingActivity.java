package com.github.sewerina.meter_readings.ui.backup_copying;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.sewerina.meter_readings.R;
import com.github.sewerina.meter_readings.ReadingApp;
import com.google.android.material.button.MaterialButton;

public class BackupCopyingActivity extends AppCompatActivity {
    private static final String TAG = "BackupCopyingActivity";
    private BackupCopyingViewModel mViewModel;
    private MaterialButton mBackupMatBtn;
    private MaterialButton mRestoreDbMatBtn;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_copying);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mBackupMatBtn = findViewById(R.id.matBtn_backup);
        mRestoreDbMatBtn = findViewById(R.id.matBtn_restore);

        mViewModel = ViewModelProviders.of(this).get(BackupCopyingViewModel.class);
        ReadingApp.sMainComponent.inject(mViewModel);

        mViewModel.getIsRefreshing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                mSwipeRefreshLayout.setRefreshing(value);
            }
        });

        mViewModel.getIsAvailable().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                mBackupMatBtn.setEnabled(value);
                mRestoreDbMatBtn.setEnabled(value);
            }
        });

        mViewModel.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (!message.isEmpty()) {
                    Toast.makeText(BackupCopyingActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mBackupMatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.backup();
            }
        });

        mRestoreDbMatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.restoreDb();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
