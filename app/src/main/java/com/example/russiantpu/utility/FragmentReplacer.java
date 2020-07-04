package com.example.russiantpu.utility;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.russiantpu.R;
import com.example.russiantpu.fragments.ArticleFragment;
import com.example.russiantpu.fragments.FeedFragment;
import com.example.russiantpu.fragments.LinksFragment;
import com.example.russiantpu.items.FeedItem;
import com.example.russiantpu.items.Item;

//класс осуществляет переход в новый фрагмент
public class FragmentReplacer {

    private final FragmentManager fragmentManager;

    private final String fragmentTag = "PREV_FRAGMENT";

    public FragmentReplacer(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    //метод загрузки фрагмента на основне передаваемого предмета
    public void goToFragment(Item item) {
        Fragment fragment;
        Bundle args = new Bundle();
        args.putString("id", item.getId()); //фрагмент формируется на основе переданного айди
        switch (item.getType()) {
            case LINKS_LIST: //список ссылок на следующие пункты
                fragment = new LinksFragment();
                replaceFragment(fragment, args);
                break;
            case FEED_LIST: //список статей
                fragment = new FeedFragment();
                replaceFragment(fragment, args);
                break;
            case ARTICLE: //статья
                fragment = new ArticleFragment();
                FeedItem article = (FeedItem)item;
                args.putString("header", article.getHeader());
                args.putString("date", article.getDate());
                replaceFragment(fragment, args);
                break;
            case LINK: //ссылка на сайт

                break;
        }
    }

    private void replaceFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                fragment).addToBackStack(fragmentTag).commit();
    }
}
