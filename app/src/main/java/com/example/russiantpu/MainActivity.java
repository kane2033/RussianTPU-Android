package com.example.russiantpu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.russiantpu.items.DrawerItem;
import com.example.russiantpu.utility.FragmentReplacer;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private List<DrawerItem> drawerItems;
    private FragmentReplacer fragmentReplacer;
    private FragmentManager fragmentManager;

    private List<DrawerItem> getDrawerItems() {
        List<DrawerItem> items;
        final Type listType = new TypeToken<ArrayList<DrawerItem>>(){}.getType();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://109.123.155.178:8080/menu/static")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonStr = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            items = new Gson().fromJson(jsonStr, listType);
                            System.out.println("Количество предметов: " + items.size());
                            Log.d("GET_REQUEST","Количество предметов: " + items.size());
                        }
                    });
                }
            }
        });
        //items.add(new DrawerItem(0, "Учёба", ContentType.LINKS_LIST));
        return items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        try {
            drawerItems = getDrawerItems(); //берём с сервиса список пунктов 1 уровня
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("GET_REQUEST", "Ошибка при попытке получить пункты меню");
        }

        Menu menu = navigationView.getMenu();
        //заполняем боковое меню пунктами 1 уровня
        for (DrawerItem item: drawerItems) {
            menu.add(1, item.getPosition(), 0, item.getName());
        }

        //передаем ссылку fragmentManager в класс, осуществляющий переход между фрагментами
        fragmentManager = getSupportFragmentManager();
        fragmentReplacer = new FragmentReplacer(fragmentManager);

        toggle.syncState();
        if (savedInstanceState == null) {
            DrawerItem initialItem = drawerItems.get(0);
            fragmentReplacer.goToFragment(initialItem);
            //navigationView.setCheckedItem(R.id.nav_links);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        DrawerItem selectedItem = drawerItems.get(itemId);
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
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack(); //возврат на предыдущий фрагмент
            }
            else {
                super.onBackPressed();
            }
        }
    }


}
