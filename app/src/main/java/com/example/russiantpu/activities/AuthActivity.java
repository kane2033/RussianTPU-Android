package com.example.russiantpu.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.russiantpu.R;
import com.example.russiantpu.fragments.LoginFragment;
import com.example.russiantpu.utility.ErrorDialogService;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.LocaleService;
import com.example.russiantpu.utility.RequestService;
import com.example.russiantpu.utility.SharedPreferencesService;
import com.example.russiantpu.utility.VKAuthService;

import java.util.Locale;

public class AuthActivity extends FragmentActivity {

    private FragmentManager fragmentManager;
    private final String fragmentTag = String.valueOf(R.string.prev_auth_frag_tag);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        *
        * Отключение темной темы во всем приложении
        *
        * */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(this);
        RequestService requestService = new RequestService();

        String language = sharedPreferencesService.getLanguage().equals("") ? Locale.getDefault().getLanguage() : sharedPreferencesService.getLanguage();
        //установка языка приложения
        LocaleService.setLocale(this, language);

        setContentView(R.layout.activity_auth);

        //получение JWT токена
        String token = sharedPreferencesService.getToken();
        String email = sharedPreferencesService.getEmail();

        //если запрос успешен (код 200), вызовется коллбэк с переходом в главную активити
        //(запрос успешен, если токен валиден)
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String value) {
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); //убираем из истории активити логина
                startActivity(intent);
            }

            //иначе запускаем фрагмент логина
            @Override
            public void onError(String message) {
                goToLogin();
            }

            @Override
            public void onFailure(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.auth_error), message, fragmentManager);
                goToLogin();
            }
        };

        requestService.doRequest("token/status", language, callback, "token", token, "email", email);
    }

    //метод запуска фрагмета логина
    private void goToLogin() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                new LoginFragment()).addToBackStack(fragmentTag).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
/*        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack(); //возврат на предыдущий фрагмент
        }
        else {
            finish();
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (VKAuthService.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}