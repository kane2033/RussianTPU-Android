package ru.tpu.russiantpu.utility.dialogFragmentServices;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentManager;

import ru.tpu.russiantpu.auth.fragments.ResetPasswordFragment;
import ru.tpu.russiantpu.utility.requests.RequestService;

public class ResetPasswordDialogService {

    public static void showDialog(final String email, final String language, final RequestService requestService, final FragmentManager fragmentManager) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String tag = "DIALOG_FRAGMENT";
                Bundle args = new Bundle();
                args.putString("email", email);
                //передаем реализаацию интерфейса действий при выборе кнопок диалогового окна
                ResetPasswordFragment dialogFragment = new ResetPasswordFragment(requestService, language);
                dialogFragment.setArguments(args);
                dialogFragment.show(fragmentManager, tag);
            }
        });

    }
}
