package com.example.russiantpu.utility;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class LocaleService {

    //метод меняет язык приложения
    public static void setLocale(Activity context, String langCode) {
        Locale locale = new Locale(langCode);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        Locale.setDefault(locale);
        config.setLocale(locale);

        context.getBaseContext().getResources().updateConfiguration(config,
                context.getBaseContext().getResources().getDisplayMetrics());
    }

/*    public static void setLocale(Activity context, String langCode) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(new Locale(langCode));
        context.getApplicationContext().createConfigurationContext(configuration);

    }*/
}
