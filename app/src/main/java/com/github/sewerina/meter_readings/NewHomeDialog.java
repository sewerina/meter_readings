package com.github.sewerina.meter_readings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class NewHomeDialog extends DialogFragment {
    private static final String TAG = "NewHomeDialog";

    private MainViewModel mViewModel;

    public static void showDialog(FragmentManager manager) {
        NewHomeDialog dialog = new NewHomeDialog();
        dialog.show(manager, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        //        LayoutInflater inflater = getActivity().getLayoutInflater();

        final TextInputEditText editText = new TextInputEditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setHint("Укажите адрес Вашего дома");
        editText.setTextSize(14);
        editText.setPadding(20, 20, 20, 20);

        builder.setTitle("Новый дом")
        .setView(editText)
        .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText() != null && !editText.getText().toString().isEmpty()) {
                    HomeEntity homeEntity = new HomeEntity();
                    homeEntity.address = editText.getText().toString();
                    mViewModel.addHome(homeEntity);
                } else {
                    Toast.makeText(editText.getContext(), "Для создания необходимо указать адрес", Toast.LENGTH_SHORT).show();
                }
            }
        })
        .setNegativeButton("Отмена", null);

        return builder.create();
    }
}
