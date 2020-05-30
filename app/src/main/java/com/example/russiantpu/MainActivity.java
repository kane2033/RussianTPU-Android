package com.example.russiantpu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.russiantpu.fragments.*;
import com.example.russiantpu.utility.DrawerItem;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    //private List<String> menus = new ArrayList<>();

    private List<DrawerItem> drawerItems = new ArrayList<>();

    private FragmentManager fragmentManager;

    private List<DrawerItem> getDrawerItems() {
        /* TODO: GET запрос на сервис для получения списка пунктов из выдвигающегося меню
        *   (1 уровень) */
        List<DrawerItem> items = new ArrayList<>();
        items.add(new DrawerItem(1, "Учёба"));
        items.add(new DrawerItem(2, "Общежитие"));
        items.add(new DrawerItem(3, "Правовая поддержка"));
        items.add(new DrawerItem(4, "Контакты с ТПУ"));
        return items;
    }

    //создание фрагмента LinksFragment с передачей атрибутов
    public LinksFragment newLinksFragmentInstance(int id) {
        /*
        тут должен быть get запрос на получение содержимого выбранного пункта,
        которое будет передано в фрагмент
         */
        LinksFragment fragment = new LinksFragment();
        Bundle args = new Bundle();

        args.putInt("id", id);
        //args.putString("linksMessage", message);

        fragment.setArguments(args);
        return fragment;
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

        drawerItems = getDrawerItems(); //берём с сервиса список пунктов 1 уровня
        Menu menu = navigationView.getMenu();
        //заполняем боковое меню пунктами 1 уровня
        for (DrawerItem item: drawerItems) {
            menu.add(1, item.getId(), 0, item.getName());
        }
        fragmentManager = getSupportFragmentManager();

        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    newLinksFragmentInstance(drawerItems.get(0).getId())).commit();
            //navigationView.setCheckedItem(R.id.nav_links);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                newLinksFragmentInstance(itemId)).commit();
        //int itemGroup = item.getGroupId();
/*        if (itemId == R.id.nav_links) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LinksFragment()).commit();
        }*/

/*        switch (itemId) {
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LinksFragment()).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FeedFragment()).commit();
                break;
        }*/

/*        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;

                case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                break;
        }*/
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
