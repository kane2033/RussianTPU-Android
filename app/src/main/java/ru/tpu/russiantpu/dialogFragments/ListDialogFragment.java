package ru.tpu.russiantpu.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.utility.callbacks.ListDialogCallback;

public class ListDialogFragment extends DialogFragment {

    private final ListDialogCallback callback; //интерфейс содержит действия при нажатии на элемент списка
    private final int titleResId; //заголовок фрагмента
    private final String[] list; //отображаемый список

    public ListDialogFragment(ListDialogCallback callback, int titleResId, String[] list) {
        this.callback = callback;
        this.titleResId = titleResId;
        this.list = list;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //настройка фрагмента
        builder.setTitle(titleResId)
                .setItems(list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) { //если юзер выбрал предмет списка
                        callback.onItemClick(list[index]); //возвращаем через коллбэк выбранный предмет
                    }
                }) //кнопка отмены
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss(); //закрываем диалог
                    }
                });
        return builder.create();
    }
}
