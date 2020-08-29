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
import androidx.core.widget.ContentLoadingProgressBar;
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
    private ContentLoadingProgressBar progressBar;
    private RequestService requestService;

    private Article article;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //отображаем в фрагменте
        LinearLayout layoutInflater = (LinearLayout) inflater.inflate(R.layout.fragment_article, container, false);
        webView = layoutInflater.findViewById(R.id.fullArticle); //статья
        missingContentText = layoutInflater.findViewById(R.id.missingContentText);
        frameLayout = layoutInflater.findViewById(R.id.fullscreen_container); //контейнер для полноэкранного режима
        progressBar = getActivity().findViewById(R.id.progress_bar);
        return layoutInflater;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //вспомогательные классы
        final Activity activity = getActivity();
        final SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(activity);
        final GsonService gsonService = new GsonService();
        requestService = new RequestService(sharedPreferencesService);

        String selectedArticleId = getArguments().getString("id");

        progressBar.show(); //включаем прогресс бар

        //реализация коллбека - что произойдет при получении данных с сервера
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                article = gsonService.fromJsonToObject(jsonBody, Article.class);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (article.getSubject() == null) { //если нет контента, уведомляем
                            missingContentText.setText(R.string.missing_content);
                        }
                        else { //иначе заполняем фрагмент содержимым статьи
                            //установка заголовка в тулбаре
                            activity.setTitle(article.getTopic());

                            //настройки для отображения видео
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setWebChromeClient(new ChromeClient(webView, frameLayout));

                            //отображение в формате html
                            webView.loadData(article.getText(), "text/html; charset=utf-8", "UTF-8");
                        }
                        progressBar.hide();
                    }
                });
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.article_error), message, getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.article_error), message, getFragmentManager());
            }
        };

        //получение JWT токена
        String token = sharedPreferencesService.getToken();
        String language = sharedPreferencesService.getLanguage();
        //запрос за получение списка статей по айди пункта меню
        requestService.doRequest("article/" + selectedArticleId, callback, token, language, "fromMenu", "true");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //при закрытии фрагмента отменяем все запросы
        requestService.cancelAllRequests();
        //выключаем прогрессбар
        progressBar.hide();
    }
}
