package com.github.sewerina.meter_readings.ui.homes

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.NewHomeEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewHomeDialog : DialogFragment() {
    private val mViewModel: HomesViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_home, null)
        val addressEt: TextInputEditText = view.findViewById(R.id.et_homeAddress)
        builder.setTitle(R.string.title_createHome)
            .setView(view)
            .setPositiveButton(R.string.btn_save) { dialog, which ->
                if (addressEt.text != null && !addressEt.text.toString().isEmpty()) {
                    val homeEntity = NewHomeEntity()
                    homeEntity.address = addressEt.text.toString()
                    mViewModel.addHome(homeEntity)
                } else {
                    Toast.makeText(addressEt.context, R.string.toast_createHome, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton(R.string.btn_cancel, null)
        return builder.create()
    }

    companion object {
        private const val TAG = "NewHomeDialog"
        fun showDialog(manager: FragmentManager) {
            val dialog = NewHomeDialog()
            dialog.show(manager, TAG)
        }
    }
}