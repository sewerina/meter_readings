package com.github.sewerina.meter_readings.ui.readings_main

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.NewReadingEntity
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NewReadingDialog : DialogFragment() {
    private val mReadingsVM: ReadingsViewModel by activityViewModels()

    private val mSelectHomeVM: SelectHomeViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_new_reading, null)
        ReadingPreferences().setLayoutVisibility(view)

        val coldWaterEt: TextInputEditText = view.findViewById(R.id.et_coldWater)
        val hotWaterEt: TextInputEditText = view.findViewById(R.id.et_hotWater)
        val drainWaterEt: TextInputEditText = view.findViewById(R.id.et_drainWater)
        val electricityEt: TextInputEditText = view.findViewById(R.id.et_electricity)
        val gasEt: TextInputEditText = view.findViewById(R.id.et_gas)

        var coldWaterValue = 0
        var hotWaterValue = 0

        coldWaterEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//                if (s != null && !s.toString().isEmpty()) {
//                    mColdWaterValue = Integer.parseInt(s.toString());
//                    Log.d(TAG, "beforeTextChanged: coldWaterEt");
//                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    coldWaterValue = s.toString().toInt()
                    Log.d(TAG, "onTextChanged: coldWaterEt")
                }
            }

            override fun afterTextChanged(str: Editable) {}
        })
        hotWaterEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//                if (s != null && !s.toString().isEmpty()) {
//                    mHotWaterValue = Integer.parseInt(s.toString());
//                    Log.d(TAG, "beforeTextChanged: hotWaterEt");
//                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    hotWaterValue = s.toString().toInt()
                    Log.d(TAG, "onTextChanged: hotWaterEt")
                }
            }

            override fun afterTextChanged(str: Editable) {}
        })
        drainWaterEt.setOnTouchListener { v, event ->
            val drainWaterValue = coldWaterValue + hotWaterValue
            drainWaterEt.setText(drainWaterValue.toString())
            true
        }

        builder.setTitle(R.string.title_createReading)
            .setView(view)
            .setPositiveButtonIcon(
                AppCompatResources.getDrawable(
                    view.context,
                    R.drawable.ic_positive_btn
                )
            )
            .setPositiveButton("") { dialog, which ->
                val entity = NewReadingEntity(Date())

                if (coldWaterEt.text != null && coldWaterEt.text.toString().isNotEmpty()) {
                    entity.coldWater = coldWaterEt.text.toString().toInt()
                }
                if (hotWaterEt.text != null && hotWaterEt.text.toString().isNotEmpty()) {
                    entity.hotWater = hotWaterEt.text.toString().toInt()
                }
                if (drainWaterEt.text != null && drainWaterEt.text.toString().isNotEmpty()) {
                    entity.drainWater = drainWaterEt.text.toString().toInt()
                }
                if (electricityEt.text != null && electricityEt.text.toString().isNotEmpty()) {
                    entity.electricity = electricityEt.text.toString().toInt()
                }
                if (gasEt.text != null && gasEt.text.toString().isNotEmpty()) {
                    entity.gas = gasEt.text.toString().toInt()
                }
                mReadingsVM.addReading(mSelectHomeVM.getCurrentHomeEntity(), entity)
            }
        return builder.create()
    }

    companion object {
        private const val TAG = "NewReadingDialog"
        fun showDialog(manager: FragmentManager) {
            val dialog = NewReadingDialog()
            dialog.show(manager, TAG)
        }
    }
}