package com.example.russiantpu.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;


//класс, отвечающий за трансформирование
//строки в картинку
public class ImageConverter {

    //из строки base64 в Bitmap
    public Bitmap stringToBitmap(String strBase64) {
        if (strBase64 == null || strBase64.isEmpty()) {
            return null;
        }
        else {
            byte[] decodedString = Base64.decode(strBase64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
    }

    public String bitmapToString(Bitmap bitmap) {
        return "";
    }
}
