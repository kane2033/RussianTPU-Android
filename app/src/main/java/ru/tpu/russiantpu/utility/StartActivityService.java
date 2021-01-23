package ru.tpu.russiantpu.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.auth.activities.AuthActivity;

//класс осуществляет переходы между активити
public class StartActivityService {
    private final Context context;

    public StartActivityService(Context context) {
        this.context = context;
    }

    //метод перехода в активити аунтефикации
    // при истекшем токене и отсутствии рефреш токена
    public void startAuthActivityTokenExpired() {
        ToastService toastService = new ToastService(context);
        toastService.showToast(R.string.token_expired_error);
        if (context instanceof Activity) {
            ((Activity) context).finishAffinity(); //закрываем текущую активити
            // Удаляем из памяти информацию о юзере (в том числе токен)
            Activity activity = (Activity) context;
            SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(activity);
            sharedPreferencesService.clearCredentials();
        }
        Intent intent = new Intent(context, AuthActivity.class);
        context.startActivity(intent);
    }
}
