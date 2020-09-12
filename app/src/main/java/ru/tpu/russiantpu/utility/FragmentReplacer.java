package ru.tpu.russiantpu.utility;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.main.fragments.ArticleFragment;
import ru.tpu.russiantpu.main.fragments.FeedFragment;
import ru.tpu.russiantpu.main.fragments.LinksFragment;
import ru.tpu.russiantpu.main.items.FeedItem;
import ru.tpu.russiantpu.main.items.Item;
import ru.tpu.russiantpu.main.items.LinkItem;

//класс осуществляет переход в новый фрагмент
public class FragmentReplacer {

    private final FragmentManager fragmentManager;
    private AppCompatActivity activity;
    private final String mainFragmentTag = "PREV_FRAGMENT";
    private final String authFragmentTag = "PREV_AUTH_FRAGMENT";

    public FragmentReplacer(AppCompatActivity activity) {
        this.activity = activity; //активити нужна для запуска браузера через интент
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    //метод возвращает соответствующий фрагмент в соответствии с типом класса Item
    private Fragment getFragmentByItem(Item item) {
        Fragment fragment;
        Bundle args = new Bundle();
        LinkItem castedItem;
        switch (item.getType()) {
            case LINK: //ссылка на сайт
                castedItem = (LinkItem) item;
                if (castedItem.getUrl() != null && !castedItem.getUrl().isEmpty()) { //открываем браузер только при наличии ссылки
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((LinkItem) item).getUrl())));
                }
                return null;
            case LINKS_LIST: //список ссылок на следующие пункты
                fragment = new LinksFragment();
                castedItem = (LinkItem) item;
                ArrayList<LinkItem> children = castedItem.getChildren();
                //передача дочерних пунктов и заголовка в след. фрагмент
                args.putParcelableArrayList("children", children);
                //передача названия выбранного пункта для установки в тулбаре
                args.putString("header", castedItem.getName());
                fragment.setArguments(args);
                return fragment;
            case FEED_LIST: //список статей
                fragment = new FeedFragment();
                castedItem = (LinkItem) item;
                //передаем айди выбранного пункта
                args.putString("id", castedItem.getId());
                //передача названия выбранного пункта для установки в тулбаре
                args.putString("header", castedItem.getName());
                fragment.setArguments(args);
                return fragment;
            case ARTICLE: //статья
                fragment = new ArticleFragment();
                //передаем айди выбранного пункта
                if (item instanceof FeedItem) { //если переход был совершен из FEED_LIST
                    FeedItem feedItem = (FeedItem) item;
                    args.putString("id", feedItem.getId());
                }
                else { //переход из FEED_LIST
                    castedItem = (LinkItem) item;
                    args.putString("id", castedItem.getIdArticle());
                }
                fragment.setArguments(args);
                return fragment;
                default:
                    return null;
        }
    }

    //метод установки изначального фрагмента без добавления в стэк
    public void setInitialFragment(Item item) {
        Fragment fragment = getFragmentByItem(item);
        int i = fragmentManager.getBackStackEntryCount();
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    fragment).commit();
        }
    }

    //метод вывода фргамента в соответствии с item
    public void goToFragment(Item item) {
        Fragment fragment = getFragmentByItem(item);
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    fragment).addToBackStack(mainFragmentTag).commit();
        }
    }
}
