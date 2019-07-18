package com.github.sewerina.meter_readings.ui.readings_main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.github.sewerina.meter_readings.R;
import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;


public class BottomSheetReadingDialog extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetReadingDialog";
    private MainViewModel mViewModel;
    private ReadingEntity mReadingEntity;

    public static void showDialog(FragmentManager manager, ReadingEntity entity) {
        BottomSheetReadingDialog dialog = new BottomSheetReadingDialog();
        Bundle args = new Bundle();
        args.putSerializable("reading", entity);
        dialog.setArguments(args);
        dialog.show(manager, TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mReadingEntity = (ReadingEntity) getArguments().getSerializable("reading");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_delete_reading, container, false);

        MaterialButton editBtn = view.findViewById(R.id.btn_edit);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditReadingDialog.showDialog(getFragmentManager(), mReadingEntity);
                dismiss();
            }
        });

        MaterialButton deleteBtn = view.findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.deleteReading(mReadingEntity);
                dismiss();
            }

        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
