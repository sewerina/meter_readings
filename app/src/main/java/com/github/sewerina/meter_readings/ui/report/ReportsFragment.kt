package com.github.sewerina.meter_readings.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.databinding.FragmentReportsBinding
import com.github.sewerina.meter_readings.ui.readings_main.ReadingPreferences
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeViewModel
import javax.inject.Inject

class ReportsFragment : Fragment() {
    private var _binding: FragmentReportsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var mReportsVM: ReportViewModel

    @Inject
    lateinit var mSelectHomeVM: SelectHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ReadingApp.sMainComponent?.inject(this)

        val reportAdapter = ReportAdapter()
        binding.recyclerReports.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = reportAdapter
        }

        binding.fabAddReport.setOnClickListener {
            mReportsVM.addReport(mSelectHomeVM.getCurrentHomeEntity())
        }

        mReportsVM.reports.observe(viewLifecycleOwner) { reports -> reportAdapter.update(reports) }

        mSelectHomeVM.currentHomePosition.observe(viewLifecycleOwner) {
            mReportsVM.loadReports(mSelectHomeVM.getCurrentHomeEntity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                mSelectHomeVM.getCurrentHomeEntity().address
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
}