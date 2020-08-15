package com.example.russiantpu.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.russiantpu.R;
import com.example.russiantpu.dto.CheckTokenDTO;
import com.example.russiantpu.fragments.LoginFragment;
import com.example.russiantpu.utility.ErrorDialogService;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
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
        GsonService gsonService = new GsonService();

        //получение JWT токена
        String token = sharedPreferencesService.getToken();
        String email = sharedPreferencesService.getEmail();
        String json = gsonService.fromObjectToJson(new CheckTokenDTO(token, email));
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
                //ErrorDialogService.showDialog(getResources().getString(R.string.auth_error), "Test message\nanother one\nand another one", fragmentManager);
            }

            @Override
            public void onFailure(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.auth_error), message, fragmentManager);
                goToLogin();
            }
        };

        requestService.doPostRequest("auth/check", callback, language, json);
    }

    //метод запуска фрагмета логина
    private void goToLogin() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                new LoginFragment()).addToBackStack(fragmentTag).commit();
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments > 1) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            if (fragments == 1) {
                finishAffinity();
            }
            else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (VKAuthService.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}