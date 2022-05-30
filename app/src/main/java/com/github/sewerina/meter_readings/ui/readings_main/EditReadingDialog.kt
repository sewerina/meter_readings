package com.github.sewerina.meter_readings.ui.readings_main

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github.sewerina.meter_readings.FormattedDate
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import javax.inject.Inject

class EditReadingDialog : DialogFragment() {
    @Inject
    lateinit var mViewModel: MainViewModel

    private val mCalendar = Calendar.getInstance()
    private val mDatePickerListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = monthOfYear
        mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        setUpdateDate()
    }
    private lateinit var mDateEt: TextInputEditText
    private var mIsDateChanged = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        ReadingApp.sMainComponent!!.inject(this)

        val readingEntity = requireArguments().getSerializable("reading") as ReadingEntity

        val builder = MaterialAlertDialogBuilder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_edit_reading, null)
        ReadingPreferences().setLayoutVisibility(view)

        mDateEt = view.findViewById(R.id.et_date)
        val coldWaterEt: TextInputEditText = view.findViewById(R.id.et_coldWater)
        val hotWaterEt: TextInputEditText = view.findViewById(R.id.et_hotWater)
        val drainWaterEt: TextInputEditText = view.findViewById(R.id.et_drainWater)
        val electricityEt: TextInputEditText = view.findViewById(R.id.et_electricity)
        val gasEt: TextInputEditText = view.findViewById(R.id.et_gas)

        mDateEt.setOnClickListener { view -> showDatePicker(view.context) }
        mDateEt.setText(FormattedDate(readingEntity.date).text())

        coldWaterEt.setText(readingEntity.coldWater.toString())
        hotWaterEt.setText(readingEntity.hotWater.toString())
        drainWaterEt.setText(readingEntity.drainWater.toString())
        electricityEt.setText(readingEntity.electricity.toString())
        gasEt.setText(readingEntity.gas.toString())

        builder.setTitle(R.string.title_editReading)
            .setView(view)
            .setPositiveButtonIcon(
                AppCompatResources.getDrawable(
                    view.context,
                    R.drawable.ic_positive_btn
                )
            )
            .setPositiveButton("") { dialog, which ->
                if (mDateEt.text != null && mDateEt.text.toString()
                        .isNotEmpty() && mIsDateChanged
                ) {
                    readingEntity.date = mCalendar.time
                    mIsDateChanged = false
                }
                if (coldWaterEt.text != null && coldWaterEt.text.toString().isNotEmpty()) {
                    readingEntity.coldWater = coldWaterEt.text.toString().toInt()
                }
                if (hotWaterEt.text != null && hotWaterEt.text.toString().isNotEmpty()) {
                    readingEntity.hotWater = hotWaterEt.text.toString().toInt()
                }
                if (drainWaterEt.text != null && drainWaterEt.text.toString().isNotEmpty()) {
                    readingEntity.drainWater = drainWaterEt.text.toString().toInt()
                }
                if (electricityEt.text != null && electricityEt.text.toString().isNotEmpty()) {
                    readingEntity.electricity = electricityEt.text.toString().toInt()
                }
                if (gasEt.text != null && gasEt.text.toString().isNotEmpty()) {
                    readingEntity.gas = gasEt.text.toString().toInt()
                }
                mViewModel.updateReading(readingEntity)
            }
        return builder.create()
    }

    private fun showDatePicker(context: Context) {
        val datePicker = DatePickerDialog(
            context,
            mDatePickerListener,
            mCalendar[Calendar.YEAR],
            mCalendar[Calendar.MONTH],
            mCalendar[Calendar.DAY_OF_MONTH]
        )
        datePicker.show()
    }

    private fun setUpdateDate() {
        mIsDateChanged = true
        mDateEt.setText(FormattedDate(mCalendar.time).text())
    } //    private void hideKeyboardFrom(final View view) {

    //        view.post(new Runnable() {
    //            @Override
    //            public void run() {
    //                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
    //                if (imm != null) {
    //                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    //                }
    //            }
    //        });
    //    }
    companion object {
        private const val TAG = "EditReadingDialog"
        fun showDialog(manager: FragmentManager, entity: ReadingEntity) {
            val dialog = EditReadingDialog()
            val args = Bundle()
            args.putSerializable("reading", entity)
            dialog.arguments = args
            dialog.show(manager, TAG)
        }
    }
}