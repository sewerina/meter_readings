package com.github.sewerina.meter_readings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import java.util.Date;


public class NewReadingDialog extends DialogFragment {
    private static final String TAG = "NewReadingDialog";
    private MainViewModel mViewModel;

    public static void showDialog(FragmentManager manager) {
        NewReadingDialog dialog = new NewReadingDialog();
        dialog.show(manager, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_reading, null);
        final EditText coldWaterEt = view.findViewById(R.id.et_coldWater);
        final EditText hotWaterEt = view.findViewById(R.id.et_hotWater);
        final EditText drainWaterEt = view.findViewById(R.id.et_drainWater);
        final EditText electricityEt = view.findViewById(R.id.et_electricity);
        final EditText gasEt = view.findViewById(R.id.et_gas);

        builder.setView(view)
                .setPositiveButtonIcon(getActivity().getDrawable(R.drawable.ic_positive_btn))
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        int coldWater;
//                        int hotWater;
//                        int drainWater;
//                        int electricity;
//                        int gas;
                        ReadingEntity entity = new ReadingEntity();
                        entity.date = new Date();
                        if (!coldWaterEt.getText().toString().isEmpty()) {
                            entity.coldWater = Integer.parseInt(coldWaterEt.getText().toString());
                        }
                        if (!hotWaterEt.getText().toString().isEmpty()) {
                            entity.hotWater = Integer.parseInt(hotWaterEt.getText().toString());
                        }
                        if (!drainWaterEt.getText().toString().isEmpty()) {
                            entity.drainWater = Integer.parseInt(drainWaterEt.getText().toString());
                        }
                        if (!electricityEt.getText().toString().isEmpty()) {
                            entity.electricity = Integer.parseInt(electricityEt.getText().toString());
                        }
                        if (!gasEt.getText().toString().isEmpty()) {
                            entity.gas = Integer.parseInt(gasEt.getText().toString());
                        }

                        mViewModel.addReading(entity);
                    }
                });


        return builder.create();
    }
}
