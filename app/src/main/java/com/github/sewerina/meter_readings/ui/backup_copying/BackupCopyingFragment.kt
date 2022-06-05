package com.github.sewerina.meter_readings.ui.backup_copying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.databinding.FragmentBackupCopyingBinding
import javax.inject.Inject

class BackupCopyingFragment : Fragment() {
    private var _binding: FragmentBackupCopyingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var mViewModel: BackupCopyingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBackupCopyingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ReadingApp.sMainComponent?.inject(this)

        mViewModel.isRefreshing.observe(viewLifecycleOwner) { value ->
            binding.swipeRefreshLayout.isRefreshing = value
        }

        mViewModel.isAvailable.observe(viewLifecycleOwner) { value ->
            binding.matBtnBackup.isEnabled = value
            binding.matBtnRestore.isEnabled = value
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.matBtnBackup.setOnClickListener { mViewModel.backup() }
        binding.matBtnRestore.setOnClickListener { mViewModel.restoreDb() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}