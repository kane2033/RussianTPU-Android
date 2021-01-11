package ru.tpu.russiantpu.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.auth.activities.AuthActivity;
import ru.tpu.russiantpu.main.fragmentAdapters.ProfileFragmentsAdapter;
import ru.tpu.russiantpu.utility.LocaleService;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.StartActivityService;
import ru.tpu.russiantpu.utility.callbacks.DialogCallback;
import ru.tpu.russiantpu.utility.dialogFragmentServices.DialogService;
import ru.tpu.russiantpu.utility.notifications.FirebaseNotificationService;
import ru.tpu.russiantpu.utility.requests.RequestService;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //установка языка интерфейса приложения
        final SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(this);
        LocaleService.setLocale(this, sharedPreferencesService.getLanguageName());

        setContentView(R.layout.activity_profile);

        //нажатие кнопки назад
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageButton logoutButton = findViewById(R.id.button_logout);
        //выход из учетной записи юзера
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //действия при нажатии кнопок диалогового окна
                DialogCallback dialogCallback = new DialogCallback() {
                    @Override
                    public void onPositiveButton() { //выходим из учетной записи
                        FirebaseNotificationService.unsubscribeToAllNotifications(); //отписка от группы news_all
                        FirebaseNotificationService.unsubscribeFromNotifications(sharedPreferencesService.getLanguageName()); //отписываемся от рассылки уведомлений по языку
                        final RequestService requestService = new RequestService(sharedPreferencesService, new StartActivityService(ProfileActivity.this));
                        FirebaseNotificationService.unsubscribeUserFromNotifications(requestService, sharedPreferencesService.getEmail(), sharedPreferencesService.getLanguageId()); //отписываеся от уведомлений для конкретного юзера
                        sharedPreferencesService.clearCredentials(); //удаляем из памяти инфу о юзере
                        //переходим на авторизацию
                        Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
                        startActivity(intent);
                        finishAffinity(); //закрываем активити профиля и главную
                    }

                    @Override
                    public void onNegativeButton() {
                        //ничего не делаем
                    }
                };
                DialogService.showDialog(getResources().getString(R.string.profile_logout), getResources().getString(R.string.profile_logout_confirm), getSupportFragmentManager(), dialogCallback);
            }
        });

        //настраиваем ViewPager (две вкладки фрагментов в одной активити)
        ProfileFragmentsAdapter adapter = new ProfileFragmentsAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, getResources());
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
