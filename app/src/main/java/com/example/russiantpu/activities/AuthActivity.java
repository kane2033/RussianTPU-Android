package com.example.russiantpu.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.russiantpu.R;
import com.example.russiantpu.fragments.LoginFragment;
import com.example.russiantpu.utility.ErrorDialogService;
import com.example.russiantpu.utility.GenericCallback;
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
        setContentView(R.layout.activity_auth);

        SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(this);
        RequestService requestService = new RequestService();

        //получение JWT токена
        String token = sharedPreferencesService.getToken();
        String email = sharedPreferencesService.getEmail();
        String language = sharedPreferencesService.getLanguage() == null ? Locale.getDefault().getLanguage() : sharedPreferencesService.getLanguage();

        //если запрос успешен (код 200), вызовется коллбэк с переходом в главную активити
        //(запрос успешен, если токен валиден)
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String value) {
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
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
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack(); //возврат на предыдущий фрагмент
        }
        else {
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (VKAuthService.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}