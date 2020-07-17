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
    private final String fragmentTag = "PREV_FRAGMENT";

    public FragmentReplacer(AppCompatActivity activity) {
        this.activity = activity;
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    //метод загрузки фрагмента на основне передаваемого предмета
    public void goToFragment(Item item) {
        Fragment fragment;
        Bundle args = new Bundle();
        switch (item.getType()) {
            case LINKS_LIST: //список ссылок на следующие пункты
                fragment = new LinksFragment();
                ArrayList<LinkItem> children = ((LinkItem) item).getChildren();
                //передача дочерних пунктов в след. фрагмент
                args.putParcelableArrayList("children", children);
                replaceFragment(fragment, args);
                break;
            case FEED_LIST: //список статей
                fragment = new FeedFragment();
                //передаем айди выбранного пункта
                args.putString("id", item.getId());
                replaceFragment(fragment, args);
                break;
            case ARTICLE: //статья
                fragment = new ArticleFragment();
                //передаем айди выбранного пункта
                if (item instanceof FeedItem) { //если переход был совершен из FEED_LIST
                    args.putString("id", item.getId());
                }
                else { //переход из FEED_LIST
                    args.putString("id", ((LinkItem) item).getIdArticle());
                }
                replaceFragment(fragment, args);
                break;
            case LINK: //ссылка на сайт
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((LinkItem) item).getUrl())));
                break;
        }
    }

    private void replaceFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                fragment).addToBackStack(fragmentTag).commit();
    }
}
