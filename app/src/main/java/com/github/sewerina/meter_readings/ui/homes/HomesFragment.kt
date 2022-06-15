package com.github.sewerina.meter_readings.ui.homes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sewerina.meter_readings.databinding.FragmentHomesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomesFragment : Fragment() {
    private var _binding: FragmentHomesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mViewModel: HomesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeAdapter = HomeAdapter(mViewModel)
        binding.recyclerHomes.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = homeAdapter
        }

        binding.fabAddHome.setOnClickListener {
            NewHomeDialog.showDialog(parentFragmentManager)
        }

        mViewModel.homes.observe(viewLifecycleOwner) { homeEntities ->
            homeAdapter.update(
                homeEntities
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}