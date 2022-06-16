package com.github.sewerina.meter_readings.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sewerina.meter_readings.databinding.FragmentReportsBinding
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportsFragment : Fragment() {
    private var _binding: FragmentReportsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mReportsVM: ReportViewModel by viewModels()

    private val mSelectHomeVM: SelectHomeViewModel by activityViewModels()

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

        val reportAdapter = ReportAdapter(mSelectHomeVM)
        binding.recyclerReports.apply {
            layoutManager = LinearLayoutManager(view.context)
            addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))
            adapter = reportAdapter
        }

        binding.fabAddReport.setOnClickListener {
            mReportsVM.addReport(mSelectHomeVM.getCurrentHomeEntity())
        }

        mReportsVM.reports.observe(viewLifecycleOwner) { reports ->
            reportAdapter.update(reports)
            if (reports.isEmpty()) {
                binding.recyclerReports.visibility = View.GONE
                binding.tvEmptyReportList.visibility = View.VISIBLE
            } else {
                binding.recyclerReports.visibility = View.VISIBLE
                binding.tvEmptyReportList.visibility = View.GONE
            }
        }

        mSelectHomeVM.currentHomePosition.observe(viewLifecycleOwner) {
            mReportsVM.loadReports(mSelectHomeVM.getCurrentHomeEntity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}