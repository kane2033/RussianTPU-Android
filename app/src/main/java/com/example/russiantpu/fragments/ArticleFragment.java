package com.example.russiantpu.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.russiantpu.R;
import com.example.russiantpu.dataAdapters.ClickListener;
import com.example.russiantpu.dataAdapters.FeedDataAdapter;
import com.example.russiantpu.items.Article;
import com.example.russiantpu.items.FeedItem;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;

//фрагмент, отображающий список статей (новостей)
public class ArticleFragment extends Fragment {

    private WebView webView;
    private TextView date;

    private Article article;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //отображаем в фрагменте
        LinearLayout layoutInflater = (LinearLayout) inflater.inflate(R.layout.fragment_article, container, false);
        webView = layoutInflater.findViewById(R.id.fullArticle); //статья
        date = layoutInflater.findViewById(R.id.date); //дата создания
        return layoutInflater;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //вспомогательные классы
        final RequestService requestService = new RequestService();
        final GsonService gsonService = new GsonService();

        String selectedArticleId = getArguments().getString("id");

        //реализация коллбека - что произойдет при получении данных с сервера
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                article = gsonService.fromJsonToObject(jsonBody, Article.class);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //отображение в формате html
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setUseWideViewPort(true);
                        webView.loadData(article.getText(), "text/html; charset=utf-8", "UTF-8");
                        date.setText(article.getCreateDate());
                    }
                });

            }
        };
        //запрос за получение списка статей по айди пункта меню
        requestService.doRequest("article/" + selectedArticleId, callback, "fromMenu", "true");


    }

}
