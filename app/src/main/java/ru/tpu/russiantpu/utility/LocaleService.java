package ru.tpu.russiantpu.utility;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleService {

    //метод меняет язык приложения
    public static void setLocale(Activity activity, String langCode) {
        Locale locale = new Locale(langCode);
        Configuration config = new Configuration(activity.getResources().getConfiguration());
        Locale.setDefault(locale);
        config.setLocale(locale);
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();

        activity.getBaseContext().getResources().updateConfiguration(config,
                displayMetrics);
        //без обновления конфига application context не все строки будут переведены (пр.: ошибки в полях ввода)
        activity.getApplicationContext().getResources().updateConfiguration(config,
                displayMetrics);
    }
}
