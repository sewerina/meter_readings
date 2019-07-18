package com.github.sewerina.meter_readings.ui.backup_copying;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.github.sewerina.meter_readings.R;
import com.google.android.material.button.MaterialButton;

public class BackupCopyingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_copying);

        MaterialButton copyMatBtn = findViewById(R.id.matBtn_copy);
        MaterialButton updateMatBtn = findViewById(R.id.matBtn_update);
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
