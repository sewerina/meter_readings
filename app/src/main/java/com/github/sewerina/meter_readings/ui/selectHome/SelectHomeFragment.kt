package com.github.sewerina.meter_readings.ui.selectHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.sewerina.meter_readings.databinding.FragmentSelectHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectHomeFragment : Fragment() {
    private var _binding: FragmentSelectHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mViewModel: SelectHomeViewModel by activityViewModels()

    private val mOnItemSelectedListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                mViewModel.changeCurrentHome(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSelectHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerAdapter: ArrayAdapter<String> =
            ArrayAdapter(view.context, android.R.layout.simple_spinner_item, ArrayList())
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHomes.apply {
            adapter = spinnerAdapter
            onItemSelectedListener = mOnItemSelectedListener
        }

        mViewModel.homeEntities.observe(viewLifecycleOwner) { homeEntities ->
            spinnerAdapter.clear()
            spinnerAdapter.addAll(homeEntities.map { it.address })
            spinnerAdapter.notifyDataSetChanged()
            if (homeEntities.isNotEmpty() && !mViewModel.loaded) {
                mViewModel.load()
            }
        }
        mViewModel.currentHomePosition.observe(viewLifecycleOwner) { position ->
            binding.spinnerHomes.setSelection(position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.spinnerHomes.onItemSelectedListener = null
        _binding = null
    }
}