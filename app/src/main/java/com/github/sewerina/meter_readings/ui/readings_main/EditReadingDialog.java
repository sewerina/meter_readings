package com.github.sewerina.meter_readings.ui.readings_main;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.github.sewerina.meter_readings.FormattedDate;
import com.github.sewerina.meter_readings.R;
import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class EditReadingDialog extends DialogFragment {
    private static final String TAG = "EditReadingDialog";
    private MainViewModel mViewModel;
    private ReadingEntity mReadingEntity;

    private Calendar mCalendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener mDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setUpdateDate();
        }
    };
    private TextInputEditText mDateEt;
    private boolean mIsDateChanged = false;

    public static void showDialog(FragmentManager manager, ReadingEntity entity) {
        EditReadingDialog dialog = new EditReadingDialog();
        Bundle args = new Bundle();
        args.putSerializable("reading", entity);
        dialog.setArguments(args);
        dialog.show(manager, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mReadingEntity = (ReadingEntity) getArguments().getSerializable("reading");
        }

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_reading, null);

        new ReadingPreferences(view.getContext()).setLayoutVisibility(view);

        mDateEt = view.findViewById(R.id.et_date);
        final TextInputEditText coldWaterEt = view.findViewById(R.id.et_coldWater);
        final TextInputEditText hotWaterEt = view.findViewById(R.id.et_hotWater);
        final TextInputEditText drainWaterEt = view.findViewById(R.id.et_drainWater);
        final TextInputEditText electricityEt = view.findViewById(R.id.et_electricity);
        final TextInputEditText gasEt = view.findViewById(R.id.et_gas);

        mDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(view.getContext());
            }
        });

        if (mReadingEntity != null) {
            mDateEt.setText(new FormattedDate(mReadingEntity.date).text());
            coldWaterEt.setText(String.valueOf(mReadingEntity.coldWater));
            hotWaterEt.setText(String.valueOf(mReadingEntity.hotWater));
            drainWaterEt.setText(String.valueOf(mReadingEntity.drainWater));
            electricityEt.setText(String.valueOf(mReadingEntity.electricity));
            gasEt.setText(String.valueOf(mReadingEntity.gas));
        }

        builder.setView(view)
                .setPositiveButtonIcon(getActivity().getDrawable(R.drawable.ic_positive_btn))
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDateEt.getText() != null && !mDateEt.getText().toString().isEmpty() && mIsDateChanged) {
                            mReadingEntity.date = mCalendar.getTime();
                            mIsDateChanged = false;
                        }

                        if (coldWaterEt.getText() != null && !coldWaterEt.getText().toString().isEmpty()) {
                            mReadingEntity.coldWater = Integer.parseInt(coldWaterEt.getText().toString());
                        }
                        if (hotWaterEt.getText() != null && !hotWaterEt.getText().toString().isEmpty()) {
                            mReadingEntity.hotWater = Integer.parseInt(hotWaterEt.getText().toString());
                        }
                        if (drainWaterEt.getText() != null && !drainWaterEt.getText().toString().isEmpty()) {
                            mReadingEntity.drainWater = Integer.parseInt(drainWaterEt.getText().toString());
                        }
                        if (electricityEt.getText() != null && !electricityEt.getText().toString().isEmpty()) {
                            mReadingEntity.electricity = Integer.parseInt(electricityEt.getText().toString());
                        }
                        if (gasEt.getText() != null && !gasEt.getText().toString().isEmpty()) {
                            mReadingEntity.gas = Integer.parseInt(gasEt.getText().toString());
                        }

                        mViewModel.updateReading(mReadingEntity);
                    }
                });


        return builder.create();
    }

    private void showDatePicker(Context context) {
        DatePickerDialog datePicker = new DatePickerDialog(
                context,
                mDatePickerListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void setUpdateDate() {
        mIsDateChanged = true;
        mDateEt.setText(new FormattedDate(mCalendar.getTime()).text());
    }

//    private void hideKeyboardFrom(final View view) {
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

}
