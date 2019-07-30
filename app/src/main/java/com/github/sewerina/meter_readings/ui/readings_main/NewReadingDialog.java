package com.github.sewerina.meter_readings.ui.readings_main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.github.sewerina.meter_readings.R;
import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class NewReadingDialog extends DialogFragment {
    private static final String TAG = "NewReadingDialog";
    private MainViewModel mViewModel;

    private int mColdWaterValue = 0;
    private int mHotWaterValue = 0;

    public static void showDialog(FragmentManager manager) {
        NewReadingDialog dialog = new NewReadingDialog();
        dialog.show(manager, TAG);
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_reading, null);

        new ReadingPreferences(view.getContext()).setLayoutVisibility(view);

        final TextInputEditText coldWaterEt = view.findViewById(R.id.et_coldWater);
        final TextInputEditText hotWaterEt = view.findViewById(R.id.et_hotWater);
        final TextInputEditText drainWaterEt = view.findViewById(R.id.et_drainWater);
        final TextInputEditText electricityEt = view.findViewById(R.id.et_electricity);
        final TextInputEditText gasEt = view.findViewById(R.id.et_gas);

        coldWaterEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if (s != null && !s.toString().isEmpty()) {
//                    mColdWaterValue = Integer.parseInt(s.toString());
//                    Log.d(TAG, "beforeTextChanged: coldWaterEt");
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().isEmpty()) {
                    mColdWaterValue = Integer.parseInt(s.toString());
                    Log.d(TAG, "onTextChanged: coldWaterEt");
                }
            }

            @Override
            public void afterTextChanged(Editable str) {
            }
        });

        hotWaterEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if (s != null && !s.toString().isEmpty()) {
//                    mHotWaterValue = Integer.parseInt(s.toString());
//                    Log.d(TAG, "beforeTextChanged: hotWaterEt");
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().isEmpty()) {
                    mHotWaterValue = Integer.parseInt(s.toString());
                    Log.d(TAG, "onTextChanged: hotWaterEt");
                }
            }

            @Override
            public void afterTextChanged(Editable str) {
            }
        });

        drainWaterEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int drainWaterValue = mColdWaterValue + mHotWaterValue;
                drainWaterEt.setText(String.valueOf(drainWaterValue));
                return true;
            }
        });

        builder.setTitle(R.string.title_createReading)
                .setView(view)
                .setPositiveButtonIcon(getActivity().getDrawable(R.drawable.ic_positive_btn))
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReadingEntity entity = new ReadingEntity();
                        entity.date = new Date();
                        if (coldWaterEt.getText() != null && !coldWaterEt.getText().toString().isEmpty()) {
                            entity.coldWater = Integer.parseInt(coldWaterEt.getText().toString());
                        }
                        if (hotWaterEt.getText() != null && !hotWaterEt.getText().toString().isEmpty()) {
                            entity.hotWater = Integer.parseInt(hotWaterEt.getText().toString());
                        }
                        if (drainWaterEt.getText() != null && !drainWaterEt.getText().toString().isEmpty()) {
                            entity.drainWater = Integer.parseInt(drainWaterEt.getText().toString());
                        }
                        if (electricityEt.getText() != null && !electricityEt.getText().toString().isEmpty()) {
                            entity.electricity = Integer.parseInt(electricityEt.getText().toString());
                        }
                        if (gasEt.getText() != null && !gasEt.getText().toString().isEmpty()) {
                            entity.gas = Integer.parseInt(gasEt.getText().toString());
                        }

                        mViewModel.addReading(entity);
                    }
                });


        return builder.create();
    }
}
