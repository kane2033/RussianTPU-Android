package com.example.russiantpu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.russiantpu.R;
import com.example.russiantpu.items.Article;

//фрагмент, отображающий список статей (новостей)
public class ArticleFragment extends Fragment {

    private Article article;

    private Article getArticleFromPreview(String id, String header, String date) {
        /*
        тут должен быть get запрос на получение содержимого выбранного пункта,
        которое будет передано в фрагмент
        */
        Article articleFromPreview = new Article();
        switch (id) {
            case "stringId1":
                articleFromPreview = new Article(id, header, "<h1>Статья 1</h1> полный текст", date);
                break;
            case "stringId2":
                articleFromPreview = new Article(id, header, "<h1>Статья 2</h1> полный текст", date);
                break;
            case "stringId3":
                articleFromPreview = new Article(id, header, "<h1>Статья 31</h1> полный текст", date);
                break;
            case "stringId4":
                articleFromPreview = new Article(id, header, "<h1>Статья 4</h1> полный текст", date);
                break;
            case "stringId5":
                articleFromPreview = new Article(id, header, "<h1>Статья 5</h1> полный текст", date);
                break;
            case "stringId6":
                articleFromPreview = new Article(id, header, "<h1>Статья 6</h1> полный текст", date);
                break;
            case "stringId7":
                articleFromPreview = new Article(id, header, "<h1>Статья 7</h1> полный текст", date);
                break;
        }
        return articleFromPreview;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //формируем класс статьи из превью
        article = getArticleFromPreview(getArguments().getString("id"),
                getArguments().getString("header"),
                getArguments().getString("date"));

        //отображаем в фрагменте
        RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_article, container, false);
        final WebView webView = layoutInflater.findViewById(R.id.fullArticle);
        //отображение в формате html
        //webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(article.getFullText(), "text/html; charset=utf-8", "UTF-8");
        //webView.loadDataWithBaseURL("", article.getFullText(), "text/html", "UTF-8", "");
        return layoutInflater;
    }

}
