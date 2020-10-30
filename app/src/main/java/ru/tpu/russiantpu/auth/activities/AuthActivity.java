package ru.tpu.russiantpu.auth.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.Locale;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.auth.fragments.LoginFragment;
import ru.tpu.russiantpu.main.activities.MainActivity;
import ru.tpu.russiantpu.utility.LocaleService;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.auth.VKAuthService;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.notifications.FirebaseNotificationService;
import ru.tpu.russiantpu.utility.requests.RequestService;

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

        final SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(this);
        final RequestService requestService = new RequestService();

        final String languageShortName = sharedPreferencesService.getLanguageName().equals("") ? Locale.getDefault().getLanguage() : sharedPreferencesService.getLanguageName();
        //установка языка приложения
        LocaleService.setLocale(this, languageShortName);

        setContentView(R.layout.activity_auth);

        //получение JWT токена
        final String token = sharedPreferencesService.getToken();
        final String email = sharedPreferencesService.getEmail();

        //если запрос успешен (код 200), вызовется коллбэк с переходом в главную активити
        //(запрос успешен, если токен валиден)
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String value) {
                FirebaseNotificationService.subscribeToNotifications(languageShortName); //подписываем пользователя на уведомления по языку
                final String languageId = sharedPreferencesService.getLanguageName();
                FirebaseNotificationService.subscribeUserToNotifications(requestService, email, languageId); //подисываем конкретного юзера на уведомления
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); //закрываем активити логина
            }

            //иначе запускаем фрагмент логина
            @Override
            public void onError(String message) {
                goToLogin();
            }

            @Override
            public void onFailure(String message) {
                goToLogin();
            }
        };

        requestService.doRequest("token/status", languageShortName, callback, "token", token, "email", email);
    }

    //метод запуска фрагмета логина
    private void goToLogin() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                new LoginFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (VKAuthService.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}