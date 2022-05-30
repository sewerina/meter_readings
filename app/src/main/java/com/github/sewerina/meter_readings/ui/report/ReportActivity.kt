package com.github.sewerina.meter_readings.ui.report

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class ReportActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAddReportFab: FloatingActionButton
    private lateinit var mCurrentHomeEntity: HomeEntity

    @Inject
    lateinit var mViewModel: ReportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        ReadingApp.sMainComponent!!.inject(this)

        if (intent != null) {
            mCurrentHomeEntity =
                intent.getSerializableExtra(EXTRA_CURRENT_HOME_ENTITY) as HomeEntity
        }

        title = "Отчеты для " + mCurrentHomeEntity.address

        val reportAdapter = ReportAdapter()
        mRecyclerView = findViewById<RecyclerView?>(R.id.recyclerReports).apply {
            layoutManager = LinearLayoutManager(this@ReportActivity)
            adapter = reportAdapter
        }

        mAddReportFab = findViewById(R.id.fab_addReport)
        mAddReportFab.setOnClickListener(View.OnClickListener {
            mViewModel.addReport(mCurrentHomeEntity)
        })

        mViewModel.reports.observe(this) { reports -> reportAdapter.update(reports) }
        mViewModel.loadReports(mCurrentHomeEntity)
    }

    private inner class ReportHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mMessageTv: TextView = itemView as TextView
        private lateinit var mReport: Report
        fun bind(report: Report) {
            mReport = report
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)
            val isColdWater =
                sharedPreferences.getBoolean(ReadingPreferences.KEY_PREF_COLD_WATER, true)
            val isHotWater =
                sharedPreferences.getBoolean(ReadingPreferences.KEY_PREF_HOT_WATER, true)
            val isDrainWater =
                sharedPreferences.getBoolean(ReadingPreferences.KEY_PREF_DRAIN_WATER, true)
            val isElectricity =
                sharedPreferences.getBoolean(ReadingPreferences.KEY_PREF_ELECTRICITY, true)
            val isGas = sharedPreferences.getBoolean(ReadingPreferences.KEY_PREF_GAS, true)
            val message = mReport.reportMessage(
                isColdWater,
                isHotWater,
                isDrainWater,
                isElectricity,
                isGas,
                mCurrentHomeEntity.address
            )
            mMessageTv.text = message
        }

    }

    private inner class ReportAdapter : RecyclerView.Adapter<ReportHolder>() {
        private val mReports: MutableList<Report> = ArrayList()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportHolder {
            val textView = TextView(parent.context)
            return ReportHolder(textView)
        }

        override fun onBindViewHolder(holder: ReportHolder, position: Int) {
            holder.bind(mReports[position])
        }

        override fun getItemCount(): Int {
            return mReports.size
        }

        fun update(reports: List<Report>) {
            mReports.clear()
            mReports.addAll(reports)
            notifyDataSetChanged()
        }

        fun add(report: Report) {
            mReports.add(report)
            notifyItemInserted(mReports.size - 1)
        }
    }

    companion object {
        private const val EXTRA_CURRENT_HOME_ENTITY = "currentHomeEntity"

        @JvmStatic
        fun newIntent(context: Context, homeEntity: HomeEntity): Intent {
            val intent = Intent(context, ReportActivity::class.java)
            intent.putExtra(EXTRA_CURRENT_HOME_ENTITY, homeEntity)
            return intent
        }
    }
}