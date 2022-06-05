package com.github.sewerina.meter_readings.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.github.sewerina.meter_readings.R

class UserPreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}