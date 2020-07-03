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

import com.example.russiantpu.enums.ContentType;
import com.example.russiantpu.items.DrawerItem;
import com.example.russiantpu.utility.FragmentReplacer;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private List<DrawerItem> drawerItems;
    private FragmentReplacer fragmentReplacer;
    private FragmentManager fragmentManager;

    private List<DrawerItem> getDrawerItems() {
        /* TODO: GET запрос на сервис для получения списка пунктов из выдвигающегося меню
        *   (1 уровень) */
        List<DrawerItem> items = new ArrayList<>();
        items.add(new DrawerItem(0, "Учёба", ContentType.LINKS_LIST));
        items.add(new DrawerItem(1, "Общежитие", ContentType.LINKS_LIST));
        items.add(new DrawerItem(2, "Правовая поддержка", ContentType.LINKS_LIST));
        items.add(new DrawerItem(3, "Контакты с ТПУ", ContentType.LINKS_LIST));
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

        drawerItems = getDrawerItems(); //берём с сервиса список пунктов 1 уровня
        Menu menu = navigationView.getMenu();
        //заполняем боковое меню пунктами 1 уровня
        for (DrawerItem item: drawerItems) {
            menu.add(1, item.getId(), 0, item.getName());
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
