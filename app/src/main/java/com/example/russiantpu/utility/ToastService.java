package com.example.russiantpu.utility;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/*
* Класс используется для отображения Toast сообщений
* с помощью Handler на основне ApplicationContext
* */
public class ToastService {

    //считается лучшей практикой передавать ApplicationContext
    private Context applicationContext;

    public ToastService(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    //метод для строк из strings.xml,
    //когда нужно вывести только строку из xml
    public void showToast(int message) {
        showToast(applicationContext.getResources().getString(message));
    }

    //метод для строк из strings.xml,
    //когда необхоимдо вывести произвольную строку вместе со строкой из xml
    public void showToast(int xmlString, String message) {
        showToast(applicationContext.getResources().getString(xmlString) + message);
    }

    //отображает Toast сообщение в контексте applicationContext
    public void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
