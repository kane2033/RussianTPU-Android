package com.example.russiantpu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.russiantpu.R;
import com.example.russiantpu.dataAdapters.LinksDataAdapter;
import com.example.russiantpu.enums.ContentType;
import com.example.russiantpu.items.Article;
import com.example.russiantpu.items.LinkItem;
import com.example.russiantpu.utility.FragmentReplacer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//фрагмент, отображающий список статей (новостей)
public class ArticleFragment extends Fragment {

    private Article article;

    private Article getArticleFromPreview(int id, String header, String date) {
        /*
        тут должен быть get запрос на получение содержимого выбранного пункта,
        которое будет передано в фрагмент
        */
        Article articleFromPreview = new Article();
        switch (id) {
            case 0:
                articleFromPreview = new Article(id, header, "<h1>Статья 1</h1> полный текст", date);
                break;
            case 1:
                articleFromPreview = new Article(id, header, "<h1>Статья 2</h1> полный текст", date);
                break;
            case 2:
                articleFromPreview = new Article(id, header, "<h1>Статья 31</h1> полный текст", date);
                break;
            case 3:
                articleFromPreview = new Article(id, header, "<h1>Статья 4</h1> полный текст", date);
                break;
            case 4:
                articleFromPreview = new Article(id, header, "<h1>Статья 5</h1> полный текст", date);
                break;
            case 5:
                articleFromPreview = new Article(id, header, "<h1>Статья 6</h1> полный текст", date);
                break;
            case 6:
                articleFromPreview = new Article(id, header, "<h1>Статья 7</h1> полный текст", date);
                break;
        }
        return articleFromPreview;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //формируем класс статьи из превью
        article = getArticleFromPreview(getArguments().getInt("id"),
                getArguments().getString("header"),
                getArguments().getString("date"));

        //отображаем в фрагменте
        RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_article, container, false);
        final WebView webView = layoutInflater.findViewById(R.id.fullArticle);
        //отображение в формате html
        //webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(article.getFullText(), "text/html; charset=utf-8", "UTF-8");
        //webView.loadDataWithBaseURL("", article.getFullText(), "text/html", "UTF-8", "");

        /*        //HTTP запрос (пока тестовый)
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://reqres.in/api/users/2")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(myResponse);
                            //нужно десериализовать в объект с помощью gson.fromJson()
                        }
                    });
                }
            }
        });

        fragmentReplacer = new FragmentReplacer(getFragmentManager());

        int selectedItemId = getArguments().getInt("id", 0);
        items = getItemsById(selectedItemId);
        RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список

        //создаем адаптер
        LinksDataAdapter adapter = new LinksDataAdapter(this.getContext(), items);
        //установка действия при клике
        adapter.setOnItemClickListener(new LinksDataAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                LinkItem clickedItem = items.get(position);
                fragmentReplacer.goToFragment(clickedItem.getType(), clickedItem.getId());
            }

            //пока не используется, оставлен на будущее
            @Override
            public void onItemLongClick(int position, View v) {
            }
        });
        //устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);*/
        return layoutInflater;
    }

}
