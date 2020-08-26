package com.example.russiantpu.utility;

import android.app.Activity;

import androidx.core.widget.ContentLoadingProgressBar;

public class ProgressBarSwitcher {

    //включает/выключает прогресс бар в UI потоке
    public static void switchPB(Activity activity, final ContentLoadingProgressBar progressBar) {
        if (activity == null)
            return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //int visible = progressBar.getVisibility();
                //visible = visible;
                if (progressBar.isShown()) {
                    progressBar.hide();
                }
                else
                {
                    progressBar.show();
                }
            }
        });
    }
}
