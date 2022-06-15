package com.github.sewerina.meter_readings.ui.readings_main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetReadingDialog : BottomSheetDialogFragment() {
    private val mReadingsVM: ReadingsViewModel by activityViewModels()

    private val mSelectHomeVM: SelectHomeViewModel by activityViewModels()

    private lateinit var mReadingEntity: ReadingEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mReadingEntity = requireArguments().getSerializable(ARG_READING_ENTITY) as ReadingEntity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_edit_delete_reading, container, false)

        val editBtn: MaterialButton = view.findViewById(R.id.btn_edit)
        editBtn.setOnClickListener {
            EditReadingDialog.showDialog(parentFragmentManager, mReadingEntity)
            dismiss()
        }

        val deleteBtn: MaterialButton = view.findViewById(R.id.btn_delete)
        deleteBtn.setOnClickListener {
            mReadingsVM.deleteReading(mSelectHomeVM.getCurrentHomeEntity(), mReadingEntity)
            dismiss()
        }

        return view
    }

    companion object {
        const val ARG_READING_ENTITY = "reading"
    }
}