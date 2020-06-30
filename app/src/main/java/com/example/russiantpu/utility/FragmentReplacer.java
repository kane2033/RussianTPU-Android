package com.example.russiantpu.utility;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.russiantpu.R;
import com.example.russiantpu.enums.ContentType;
import com.example.russiantpu.fragments.FeedFragment;
import com.example.russiantpu.fragments.LinksFragment;

//класс осуществляет переход в новый фрагмент
public class FragmentReplacer {

    private final FragmentManager fragmentManager;

    private final String fragmentTag = "PREV_FRAGMENT";

    public FragmentReplacer(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void goToFragment(ContentType type, int id) {
        Fragment fragment;
        Bundle args = new Bundle();
        args.putInt("id", id); //id меню 1 уровня
        switch (type) {
            case LINKS_LIST: //список ссылок на следующие пункты
                fragment = new LinksFragment();
                replaceFragment(fragment, fragmentManager, args);
                break;
            case FEED_LIST: //список статей
                fragment = new FeedFragment();
                replaceFragment(fragment, fragmentManager, args);
                break;
            case LINK: //ссылка на сайт

                break;
            case ARTICLE: //статья

                break;
        }
    }

    private void replaceFragment(Fragment fragment, FragmentManager fragmentManager, Bundle args) {
        fragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                fragment).addToBackStack(fragmentTag).commit();
    }
}
