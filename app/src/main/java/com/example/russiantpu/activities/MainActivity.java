package com.example.russiantpu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.example.russiantpu.R;
import com.example.russiantpu.dto.UserDTO;
import com.example.russiantpu.items.LinkItem;
import com.example.russiantpu.utility.ErrorDialogService;
import com.example.russiantpu.utility.FragmentReplacer;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;
import com.example.russiantpu.utility.SharedPreferencesService;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ArrayList<LinkItem> drawerItems;
    private FragmentReplacer fragmentReplacer;
    private FragmentManager fragmentManager;
    private GsonService gsonService = new GsonService();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        final View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
/*        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //получение JWT токена и пользователя из памяти
        final SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(this);
        String token = sharedPreferencesService.getToken();
        UserDTO user = sharedPreferencesService.getUser();

        //установка действия при клике на хэдер выдвижного меню
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesService.clearCredentials(); //удаляем токен из памяти
                startActivity(new Intent(MainActivity.this, AuthActivity.class)); //переходим обратно в активити логина
                //очистка фрагментов из стека при переходе в основную активити - в противном случае, при нажатии кнопки "назад"
                //происходит непредвиденный переход в логин
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        //задаем данные пользователя в хэдере выдвижного меню
        TextView firstNameTextView = header.findViewById(R.id.firstName);
        TextView emailTextView = header.findViewById(R.id.email);
        firstNameTextView.setText(user.getFirstName());
        emailTextView.setText(user.getEmail());

        //запрос на сервис для получения пунктов выдвижного меню
        RequestService requestService = new RequestService();
        //реализация коллбека - что произойдет при получении данных с сервиса
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                //получение списка пунктов бокового меню (1 уровень)
                drawerItems = gsonService.fromJsonToArrayList(jsonBody, LinkItem.class);
                Log.d("GET_REQUEST", "Получены предметы шторки");

                //обновление элементов интерфейса в потоке UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Menu menu = navigationView.getMenu();
                        //заполняем боковое меню пунктами 1 уровня
                        for (int i = 0; i < drawerItems.size(); i++) {
                            menu.add(1, i, 0, drawerItems.get(i).getName());
                        }

                        toggle.syncState();
                        if (!drawerItems.isEmpty()) {
                            LinkItem initialItem = drawerItems.get(0);
                            fragmentReplacer.goToFragment(initialItem);
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.drawer_error), message, fragmentManager);
            }

            @Override
            public void onFailure(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.drawer_error), message, fragmentManager);
            }
        };
        //делаем запрос на получение пунктов меню на языке пользователя
        requestService.doRequest("menu", callback, token, "language", user.getLanguage());

        //передаем ссылку fragmentManager в класс,
        // осуществляющий переход между фрагментами
        fragmentManager = getSupportFragmentManager();
        fragmentReplacer = new FragmentReplacer(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        LinkItem selectedItem = drawerItems.get(itemId);
        fragmentReplacer.goToFragment(selectedItem);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); //закрытие шторки
        }
        else {
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
    }

}