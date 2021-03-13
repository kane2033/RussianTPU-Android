package ru.tpu.russiantpu.main.activities;

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
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.dto.UserDTO;
import ru.tpu.russiantpu.main.items.Item;
import ru.tpu.russiantpu.main.items.LinkItem;
import ru.tpu.russiantpu.utility.FragmentReplacer;
import ru.tpu.russiantpu.utility.LocaleService;
import ru.tpu.russiantpu.utility.MainActivityItems;
import ru.tpu.russiantpu.utility.MenuItemIconLoader;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.StartActivityService;
import ru.tpu.russiantpu.utility.ToastService;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MainActivityItems {

    private FragmentReplacer fragmentReplacer;
    private RequestService requestService;

    private DrawerLayout drawer;

    private ArrayList<LinkItem> drawerItems;
    private final String drawerItemsKey = "drawerItems";

    @NonNull
    @Override
    public List<LinkItem> getItems() {
        return drawerItems;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(this);
        requestService = new RequestService(sharedPreferencesService, new StartActivityService(this));

        //получение JWT токена и пользователя из памяти
        final String token = sharedPreferencesService.getToken();
        UserDTO user = sharedPreferencesService.getUser();
        //установка языка приложения (интерфейса)
        LocaleService.setLocale(this, user.getLanguageName());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        final View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //установка действия при клике на хэдер выдвижного меню
        header.setOnClickListener(v -> {
            //переходим в активити профиля
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        //задаем данные пользователя в хэдере выдвижного меню
        TextView firstNameTextView = header.findViewById(R.id.firstName);
        TextView emailTextView = header.findViewById(R.id.email);
        firstNameTextView.setText(user.getFirstName());
        emailTextView.setText(user.getEmail());

        //передаем ссылку fragmentManager в класс,
        // осуществляющий переход между фрагментами
        fragmentReplacer = new FragmentReplacer(this);

        //восстанавливаем элементы из временной памяти
        // (пр.: смена ориентации)
        if (savedInstanceState != null) {
            drawerItems = savedInstanceState.getParcelableArrayList(drawerItemsKey);
            populateMenu(navigationView, toggle); //заполняем боковое меню
        } else { //иначе делаем запрос на сервис
            //запрос на сервис для получения пунктов выдвижного меню
            getItemsRequest(token, user, navigationView, toggle);
        }

        // Также делаем все запросы повторно при свайпе вверх
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getItemsRequest(token, user, navigationView, toggle);
            fragmentReplacer.refreshFragment(drawerItems.get(0));
            swipeRefreshLayout.setRefreshing(false);
        });

    }

    private void getItemsRequest(String token, UserDTO user, NavigationView navigationView, ActionBarDrawerToggle toggle) {
        final ContentLoadingProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.show(); //включаем прогресс бар

        final GsonService gsonService = new GsonService();
        final ToastService toastService = new ToastService(this);

        //реализация коллбека - что произойдет при получении данных с сервиса
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                //получение списка пунктов бокового меню (1 уровень)
                drawerItems = gsonService.fromJsonToArrayList(jsonBody, LinkItem.class);
                Log.d("GET_REQUEST", "Получены предметы шторки");

                //обновление элементов интерфейса в потоке UI
                runOnUiThread(() -> {
                    progressBar.hide(); //выключаем прогресс бар

                    populateMenu(navigationView, toggle); //заполняем боковое меню

                    if (!drawerItems.isEmpty()) {
                        Item selectedItem = drawerItems.get(0);
                        fragmentReplacer.setInitialFragment(selectedItem);
                    }
                });
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар
                runOnUiThread(progressBar::hide);
                toastService.showToast(message);
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар
                runOnUiThread(progressBar::hide);
                toastService.showToast(R.string.drawer_error);
            }
        };
        //делаем запрос на получение пунктов меню на языке пользователя
        requestService.doRequest("menu", callback, token, user.getLanguageId(),
                "language", user.getLanguageId(), "email", user.getEmail());
    }

    //To keep them in memory
    final List<MenuItemIconLoader> iconLoaderList = new ArrayList<>();

    //метод заполнения боковой шторки
    private void populateMenu(NavigationView navigationView, ActionBarDrawerToggle toggle) {
        Menu menu = navigationView.getMenu();
        if (menu.size() > 0) {
            menu.clear();
            iconLoaderList.clear();
        }

        // первый элемент - календарь событий
        menu.add(0, 0, 0, getString(R.string.calendar_title));

        //заполняем боковое меню пунктами 1 уровня
        for (int i = 1; i <= drawerItems.size(); i++) {
            final MenuItem menuItem = menu.add(1, i, 0, drawerItems.get(i - 1).getName());
            iconLoaderList.add(new MenuItemIconLoader(getResources(), menuItem));
        }
        // Загрузка картинок в шторку
        for (int i = 0; i < drawerItems.size(); i++) {
            LinkItem item = drawerItems.get(i);
            MenuItemIconLoader iconLoader = iconLoaderList.get(i);
            iconLoader.load(item);
        }

        toggle.syncState();
    }

    private void loadIconsIntoMenu() {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelableArrayList(drawerItemsKey, drawerItems); //сохраняем предметы шторки
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // первый элемент - календарь событий
        if (itemId == 0) {
            startActivity(new Intent(this, CalendarActivity.class));
        } else { // иначе переходим на элемент меню, полученный с сервиса
            Item selectedItem = drawerItems.get(itemId - 1);
            fragmentReplacer.goToFragment(selectedItem);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); //закрытие шторки
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //при приостановке активити останавливаем все запросы
        if (requestService != null) {
            requestService.cancelAllRequests();
        }
    }
}
