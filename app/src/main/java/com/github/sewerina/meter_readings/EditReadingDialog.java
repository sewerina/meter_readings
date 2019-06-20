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

public class EditReadingDialog extends DialogFragment {
    private static final String TAG = "EditReadingDialog";
    private MainViewModel mViewModel;
    private ReadingEntity mReadingEntity;

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
        EditText dateEt = view.findViewById(R.id.et_date);
        final EditText coldWaterEt = view.findViewById(R.id.et_coldWater);
        final EditText hotWaterEt = view.findViewById(R.id.et_hotWater);
        final EditText drainWaterEt = view.findViewById(R.id.et_drainWater);
        final EditText electricityEt = view.findViewById(R.id.et_electricity);
        final EditText gasEt = view.findViewById(R.id.et_gas);

        if (mReadingEntity != null) {
            dateEt.setText(mReadingEntity.date.toString());
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

                        // ADD Update of date

                        if (!coldWaterEt.getText().toString().isEmpty()) {
                            mReadingEntity.coldWater = Integer.parseInt(coldWaterEt.getText().toString());
                        }
                        if (!hotWaterEt.getText().toString().isEmpty()) {
                            mReadingEntity.hotWater = Integer.parseInt(hotWaterEt.getText().toString());
                        }
                        if (!drainWaterEt.getText().toString().isEmpty()) {
                            mReadingEntity.drainWater = Integer.parseInt(drainWaterEt.getText().toString());
                        }
                        if (!electricityEt.getText().toString().isEmpty()) {
                            mReadingEntity.electricity = Integer.parseInt(electricityEt.getText().toString());
                        }
                        if (!gasEt.getText().toString().isEmpty()) {
                            mReadingEntity.gas = Integer.parseInt(gasEt.getText().toString());
                        }

                        mViewModel.updateReading(mReadingEntity);
                    }
                });


        return builder.create();
    }
}
