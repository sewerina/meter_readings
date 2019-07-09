package com.github.sewerina.meter_readings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class EditHomeDialog extends DialogFragment {
    private static final String TAG = "EditHomeDialog";

    private HomeEntity mHomeEntity;
    private MainViewModel mViewModel;

    public static void showDialog(FragmentManager manager, HomeEntity entity) {
        EditHomeDialog dialog = new EditHomeDialog();
        Bundle args = new Bundle();
        args.putSerializable("home", entity);
        dialog.setArguments(args);
        dialog.show(manager, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mHomeEntity = (HomeEntity) getArguments().getSerializable("home");
        }

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_home, null);
        final TextInputEditText addressEt = view.findViewById(R.id.et_homeAddress);

        if (mHomeEntity != null) {
            addressEt.setText(mHomeEntity.address);
        }

        builder.setTitle("Изменение информации о доме")
                .setView(view)
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (addressEt.getText() != null && !addressEt.getText().toString().isEmpty()) {
                            mHomeEntity.address = addressEt.getText().toString();
                            mViewModel.updateHome(mHomeEntity);
                        } else {
                            Toast.makeText(addressEt.getContext(), "Для обновления необходимо указать адрес", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Отмена", null);

        return builder.create();
    }
}
