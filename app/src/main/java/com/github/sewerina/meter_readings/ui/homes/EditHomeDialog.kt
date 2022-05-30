package com.github.sewerina.meter_readings.ui.homes

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.database.HomeEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import javax.inject.Inject

class EditHomeDialog : DialogFragment() {
    private lateinit var mHomeEntity: HomeEntity

    @Inject
    lateinit var mViewModel: HomesViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mHomeEntity = requireArguments().getSerializable("home") as HomeEntity

        ReadingApp.sMainComponent!!.inject(this)

        val builder = MaterialAlertDialogBuilder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_home, null)
        val addressEt: TextInputEditText = view.findViewById(R.id.et_homeAddress)
        addressEt.setText(mHomeEntity.address)

        builder.setTitle(R.string.title_editHome)
            .setView(view)
            .setPositiveButton(R.string.btn_save) { dialog, which ->
                if (addressEt.text != null && !addressEt.text.toString().isEmpty()) {
                    mHomeEntity.address = addressEt.text.toString()
                    mViewModel.updateHome(mHomeEntity)
                } else {
                    Toast.makeText(addressEt.context, R.string.toast_editHome, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton(R.string.btn_cancel, null)
        return builder.create()
    }

    companion object {
        private const val TAG = "EditHomeDialog"
        fun showDialog(manager: FragmentManager?, entity: HomeEntity?) {
            val dialog = EditHomeDialog()
            val args = Bundle()
            args.putSerializable("home", entity)
            dialog.arguments = args
            dialog.show(manager!!, TAG)
        }
    }
}