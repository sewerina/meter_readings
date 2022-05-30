package com.github.sewerina.meter_readings.ui.backup_copying

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.ReadingApp
import com.google.android.material.button.MaterialButton
import javax.inject.Inject

class BackupCopyingActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModel: BackupCopyingViewModel

    private lateinit var mBackupMatBtn: MaterialButton
    private lateinit var mRestoreDbMatBtn: MaterialButton
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_copying)
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        mBackupMatBtn = findViewById(R.id.matBtn_backup)
        mRestoreDbMatBtn = findViewById(R.id.matBtn_restore)

        ReadingApp.sMainComponent?.inject(this)
        mViewModel.isRefreshing.observe(this) { value ->
            mSwipeRefreshLayout.isRefreshing = value!!
        }
        mViewModel.isAvailable.observe(this) { value ->
            mBackupMatBtn.isEnabled = value!!
            mRestoreDbMatBtn.isEnabled = value
        }

        mSwipeRefreshLayout.setOnRefreshListener(OnRefreshListener {
            mSwipeRefreshLayout.isRefreshing = false
        })
        mBackupMatBtn.setOnClickListener(View.OnClickListener { mViewModel.backup() })
        mRestoreDbMatBtn.setOnClickListener(View.OnClickListener { mViewModel.restoreDb() })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "BackupCopyingActivity"
    }
}