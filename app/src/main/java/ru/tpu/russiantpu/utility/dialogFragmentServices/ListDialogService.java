package ru.tpu.russiantpu.utility.dialogFragmentServices;

import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentManager;

import ru.tpu.russiantpu.dialogFragments.ListDialogFragment;
import ru.tpu.russiantpu.utility.callbacks.ListDialogCallback;

/**
* Класс запускает диалог со списком (DialogFragment)
* */
public class ListDialogService {

    public static void showDialog(final int titleResId, final String[] items, final FragmentManager fragmentManager, final ListDialogCallback callback) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String tag = "DIALOG_FRAGMENT";
                ListDialogFragment dialogFragment = new ListDialogFragment(callback, titleResId, items);
                dialogFragment.show(fragmentManager, tag);
            }
        });
    }
}
