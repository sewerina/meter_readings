package com.github.sewerina.meter_readings.ui.readings_main

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import javax.inject.Inject

class BottomSheetReadingDialog : BottomSheetDialogFragment() {
    @Inject
    lateinit var mViewModel: MainViewModel

    private lateinit var mReadingEntity: ReadingEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mReadingEntity = requireArguments().getSerializable("reading") as ReadingEntity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_edit_delete_reading, container, false)

        ReadingApp.sMainComponent!!.inject(this)

        val editBtn: MaterialButton = view.findViewById(R.id.btn_edit)
        editBtn.setOnClickListener {
            EditReadingDialog.showDialog(parentFragmentManager, mReadingEntity)
            dismiss()
        }

        val deleteBtn: MaterialButton = view.findViewById(R.id.btn_delete)
        deleteBtn.setOnClickListener {
            mViewModel.deleteReading(mReadingEntity)
            dismiss()
        }

        return view
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    companion object {
        private const val TAG = "BottomSheetReadingDialog"
        fun showDialog(manager: FragmentManager, entity: ReadingEntity) {
            val dialog = BottomSheetReadingDialog()
            val args = Bundle()
            args.putSerializable("reading", entity)
            dialog.arguments = args
            dialog.show(manager, TAG)
        }
    }
}