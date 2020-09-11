package ru.tpu.russiantpu.utility;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentManager;
import ru.tpu.russiantpu.fragments.ErrorDialogFragment;

/*
* Класс запускает диалоговое окно с сообщением
* */
public class ErrorDialogService {

    public static void showDialog(final String title, final String message, final FragmentManager fragmentManager) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String tag = "DIALOG_FRAGMENT";
                Bundle args = new Bundle();
                args.putString("title", title);
                args.putString("message", message);
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                dialogFragment.setArguments(args);
                dialogFragment.show(fragmentManager, tag);
            }
        });

    }
}
