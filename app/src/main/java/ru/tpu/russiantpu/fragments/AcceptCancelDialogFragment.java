package ru.tpu.russiantpu.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.utility.DialogCallback;

public class AcceptCancelDialogFragment extends DialogFragment {

    //интерфейс содержит действия при нажатии на кнопки диалогового окна
    private final DialogCallback callback;

    public AcceptCancelDialogFragment(DialogCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //получаем заголовок и сообщение
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel(); //закрываем окно
                        callback.onPositiveButton();
                    }
                })
        .setNegativeButton(getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel(); //закрываем окно
                callback.onNegativeButton();
            }
        });
        return builder.create();
    }
}
