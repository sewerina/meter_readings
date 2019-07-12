package com.github.sewerina.meter_readings.ui.homes;

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

import com.github.sewerina.meter_readings.R;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class NewHomeDialog extends DialogFragment {
    private static final String TAG = "NewHomeDialog";

    private HomesViewModel mViewModel;

    public static void showDialog(FragmentManager manager) {
        NewHomeDialog dialog = new NewHomeDialog();
        dialog.show(manager, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getActivity()).get(HomesViewModel.class);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_home, null);
        final TextInputEditText addressEt = view.findViewById(R.id.et_homeAddress);

        builder.setTitle("Создание информации о новом доме")
                .setView(view)
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (addressEt.getText() != null && !addressEt.getText().toString().isEmpty()) {
                            HomeEntity homeEntity = new HomeEntity();
                            homeEntity.address = addressEt.getText().toString();
                            mViewModel.addHome(homeEntity);
                        } else {
                            Toast.makeText(addressEt.getContext(), "Для создания необходимо указать адрес", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Отмена", null);

        return builder.create();
    }
}
