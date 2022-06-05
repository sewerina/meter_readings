package com.github.sewerina.meter_readings.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btbUserPrefs.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_settings_to_navigation_userPreferences)
        }

        binding.btnReports.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_settings_to_navigation_reports)
        }

        binding.btnBackup.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_settings_to_navigation_backupCopying)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}