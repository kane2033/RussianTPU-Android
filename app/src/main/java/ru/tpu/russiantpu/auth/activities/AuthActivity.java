package ru.tpu.russiantpu.auth.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.Locale;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.auth.fragments.StartFragment;
import ru.tpu.russiantpu.utility.LocaleService;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.auth.VKAuthService;
import ru.tpu.russiantpu.utility.notifications.NotificationResolver;

public class AuthActivity extends FragmentActivity {

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

        final String languageShortName = sharedPreferencesService.getLanguageName().equals("") ? Locale.getDefault().getLanguage() : sharedPreferencesService.getLanguageName();
        //установка языка приложения
        LocaleService.setLocale(this, languageShortName);

        setContentView(R.layout.activity_auth);

        // Этот фрагмент будет открыт первым (стартовый)
        StartFragment startFragment = new StartFragment();

        // Передаем апп линк в фрагмент, если имеется
        String linkTo = getIntent().getStringExtra(NotificationResolver.APP_LINK_KEY);
        if (linkTo != null) {
            Bundle bundle = new Bundle();
            bundle.putString(NotificationResolver.APP_LINK_KEY, linkTo);
            startFragment.setArguments(bundle);
        }

        // Не даем активити добавить еще один фрагмент, если восстанавливаем состояние
        // (пр.: смена ориентации)
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    startFragment).commit();
        }
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