package com.example.russiantpu.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.russiantpu.R;
import com.example.russiantpu.activities.AuthActivity;

//класс осуществляет переходы между активити
public class StartActivityService {
    private Context context;

    public StartActivityService(Context context) {
        this.context = context;
    }

    //метод перехода в активити аунтефикации
    // при истекшем токене и отсутствии рефреш токена
    public void startAuthActivityTokenExpired() {
        ToastService toastService = new ToastService(context);
        toastService.showToast(R.string.token_expired_error);
        Intent intent = new Intent(context, AuthActivity.class);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finishAffinity(); //закрываем текущую активити
        }
    }
}
