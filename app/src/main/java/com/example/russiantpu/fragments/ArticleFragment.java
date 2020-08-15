package com.example.russiantpu.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.russiantpu.R;
import com.example.russiantpu.items.Article;
import com.example.russiantpu.utility.ChromeClient;
import com.example.russiantpu.utility.ErrorDialogService;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;
import com.example.russiantpu.utility.SharedPreferencesService;

//фрагмент, отображающий список статей (новостей)
public class ArticleFragment extends Fragment {

    private WebView webView;
    private TextView missingContentText;
    private FrameLayout frameLayout;

    private Article article;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //отображаем в фрагменте
        LinearLayout layoutInflater = (LinearLayout) inflater.inflate(R.layout.fragment_article, container, false);
        webView = layoutInflater.findViewById(R.id.fullArticle); //статья
        missingContentText = layoutInflater.findViewById(R.id.missingContentText);
        frameLayout = layoutInflater.findViewById(R.id.fullscreen_container); //контейнер для полноэкранного режима
        return layoutInflater;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //вспомогательные классы
        final RequestService requestService = new RequestService();
        final GsonService gsonService = new GsonService();
        final Activity activity = getActivity();

        String selectedArticleId = getArguments().getString("id");

        //реализация коллбека - что произойдет при получении данных с сервера
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                article = gsonService.fromJsonToObject(jsonBody, Article.class);

                if (article.getSubject() == null) { //если нет контента, уведомляем
                    missingContentText.setText(R.string.missing_content);
                }
                else { //иначе заполняем фрагмент содержимым статьи
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //установка заголовка в тулбаре
                            activity.setTitle(article.getTopic());

                            //настройки для отображения видео
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setWebChromeClient(new ChromeClient(webView, frameLayout));

                            //отображение в формате html
                            webView.loadData(article.getText(), "text/html; charset=utf-8", "UTF-8");
                        }
                    });
                }

            }

            @Override
            public void onError(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.article_error), message, getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.article_error), message, getFragmentManager());
            }
        };
        //получение JWT токена
        SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(activity);
        String token = sharedPreferencesService.getToken();
        String language = sharedPreferencesService.getLanguage();
        //запрос за получение списка статей по айди пункта меню
        requestService.doRequest("article/" + selectedArticleId, callback, token, language, "fromMenu", "true");


    }

}
