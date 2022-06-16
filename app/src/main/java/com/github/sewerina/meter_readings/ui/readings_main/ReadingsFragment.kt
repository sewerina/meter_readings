package com.github.sewerina.meter_readings.ui.readings_main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.databinding.FragmentReadingsBinding
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadingsFragment : Fragment() {
    private var _binding: FragmentReadingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mReadingsVM: ReadingsViewModel by activityViewModels()

    private val mSelectHomeVM: SelectHomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentReadingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoToHomes.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_readings_to_navigation_homes)
        }

        val readingAdapter = ReadingAdapter()
        binding.recyclerReadings.apply {
            layoutManager = GridLayoutManager(view.context, 2)
            adapter = readingAdapter
        }

        binding.fabAddReading.setOnClickListener {
            NewReadingDialog.showDialog(parentFragmentManager)
        }

        mReadingsVM.readings.observe(viewLifecycleOwner) { readingEntities ->
            readingAdapter.update(readingEntities)
            if (readingEntities.isEmpty()) {
                binding.recyclerReadings.visibility = View.GONE
                binding.tvEmptyReadingList.visibility = View.VISIBLE
            } else {
                binding.recyclerReadings.visibility = View.VISIBLE
                binding.tvEmptyReadingList.visibility = View.GONE
            }
        }

        mSelectHomeVM.currentHomePosition.observe(viewLifecycleOwner) {
            mReadingsVM.loadReadings(mSelectHomeVM.getCurrentHomeEntity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}