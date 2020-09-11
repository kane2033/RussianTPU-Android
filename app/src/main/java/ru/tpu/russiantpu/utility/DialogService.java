package ru.tpu.russiantpu.utility;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentManager;

import ru.tpu.russiantpu.fragments.AcceptCancelDialogFragment;

public class DialogService {

    public static void showDialog(final String title, final String message, final FragmentManager fragmentManager, final DialogCallback callback) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String tag = "DIALOG_FRAGMENT";
                Bundle args = new Bundle();
                args.putString("title", title);
                args.putString("message", message);
                //передаем реализаацию интерфейса действий при выборе кнопок диалогового окна
                AcceptCancelDialogFragment dialogFragment = new AcceptCancelDialogFragment(callback);
                dialogFragment.setArguments(args);
                dialogFragment.show(fragmentManager, tag);
            }
        });

    }
}
