package com.github.sewerina.meter_readings.ui.report

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeViewModel

class ReportAdapter(private val mSelectHomeVM: SelectHomeViewModel) :
    RecyclerView.Adapter<ReportHolder>() {
    private val mReports: MutableList<Report> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportHolder {
        val textView = TextView(parent.context)
        return ReportHolder(textView, mSelectHomeVM)
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
}