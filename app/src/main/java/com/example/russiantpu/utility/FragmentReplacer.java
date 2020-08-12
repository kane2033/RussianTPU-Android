package com.example.russiantpu.utility;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.russiantpu.R;
import com.example.russiantpu.fragments.ArticleFragment;
import com.example.russiantpu.fragments.FeedFragment;
import com.example.russiantpu.fragments.LinksFragment;
import com.example.russiantpu.items.FeedItem;
import com.example.russiantpu.items.Item;
import com.example.russiantpu.items.LinkItem;

import java.util.ArrayList;

//класс осуществляет переход в новый фрагмент
public class FragmentReplacer {

    private final FragmentManager fragmentManager;
    private final AppCompatActivity activity;
    private final String mainFragmentTag = "PREV_FRAGMENT";
    private final String authFragmentTag = "PREV_AUTH_FRAGMENT";

    public FragmentReplacer(AppCompatActivity activity) {
        this.activity = activity;
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    //метод загрузки фрагмента на основне передаваемого предмета
    public void goToFragment(Item item) {
        Fragment fragment;
        Bundle args = new Bundle();
        LinkItem castedItem;
        switch (item.getType()) {
            case LINK: //ссылка на сайт
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((LinkItem) item).getUrl())));
                break;
            case LINKS_LIST: //список ссылок на следующие пункты
                fragment = new LinksFragment();
                castedItem = (LinkItem) item;
                ArrayList<LinkItem> children = castedItem.getChildren();
                //передача дочерних пунктов и заголовка в след. фрагмент
                args.putParcelableArrayList("children", children);
                //передача названия выбранного пункта для установки в тулбаре
                args.putString("header", castedItem.getName());
                replaceFragment(fragment, args);
                break;
            case FEED_LIST: //список статей
                fragment = new FeedFragment();
                castedItem = (LinkItem) item;
                //передаем айди выбранного пункта
                args.putString("id", castedItem.getId());
                //передача названия выбранного пункта для установки в тулбаре
                args.putString("header", castedItem.getName());
                replaceFragment(fragment, args);
                break;
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
                replaceFragment(fragment, args);
                break;
        }
    }

    private void replaceFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                fragment).addToBackStack(mainFragmentTag).commit();
    }
}
