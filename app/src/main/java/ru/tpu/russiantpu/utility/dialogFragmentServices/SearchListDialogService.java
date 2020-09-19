package ru.tpu.russiantpu.utility.dialogFragmentServices;

import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentManager;

import java.util.List;

import ru.tpu.russiantpu.dialogFragments.SearchListDialogFragment;
import ru.tpu.russiantpu.utility.callbacks.ListDialogCallback;

/**
 * Класс запускает диалог со списком (DialogFragment)
 * */
public class SearchListDialogService {

    public static void showDialog(final int layoutResId, final List<String> items, final FragmentManager fragmentManager, final ListDialogCallback callback) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String tag = "DIALOG_FRAGMENT";
                SearchListDialogFragment dialogFragment = new SearchListDialogFragment(callback, layoutResId, items);
                dialogFragment.show(fragmentManager, tag);
            }
        });
    }
}
