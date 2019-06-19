package com.github.sewerina.meter_readings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;


public class EditDeleteReadingDialog extends BottomSheetDialogFragment {
    private static final String TAG = "EditDeleteReadingDialog";

    public static void showDialog(FragmentManager manager) {
        EditDeleteReadingDialog dialog = new EditDeleteReadingDialog();
        dialog.show(manager, TAG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_delete_reading, container, false);

        MaterialButton editBtn = view.findViewById(R.id.btn_edit);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        MaterialButton deleteBtn = view.findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
}
