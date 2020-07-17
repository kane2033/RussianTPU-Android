package com.example.russiantpu.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.russiantpu.R;
import com.example.russiantpu.items.Article;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;

//фрагмент, отображающий список статей (новостей)
public class ArticleFragment extends Fragment {

    private WebView webView;
    private TextView date;
    private TextView missingContentText;

    private Article article;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //отображаем в фрагменте
        LinearLayout layoutInflater = (LinearLayout) inflater.inflate(R.layout.fragment_article, container, false);
        webView = layoutInflater.findViewById(R.id.fullArticle); //статья
        date = layoutInflater.findViewById(R.id.date); //дата создания
        missingContentText = layoutInflater.findViewById(R.id.missingContentText);
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
                            activity.setTitle(article.getSubject());

                            //отображение в формате html
                            //либо картинка слишком большая, либо текст слишком маленький
                            WebSettings settings = webView.getSettings();
                            settings.setMinimumFontSize(32); //меняет размер шрифта
                            settings.setLoadWithOverviewMode(true);
                            settings.setUseWideViewPort(true);
                            settings.setBuiltInZoomControls(true);
                            settings.setDisplayZoomControls(false);

                            webView.loadData(article.getText(), "text/html; charset=utf-8", "UTF-8");
                            String dateStr = "Дата создания: " + article.getCreateDate();
                            date.setText(dateStr);
                        }
                    });
                }

            }
        };
        //запрос за получение списка статей по айди пункта меню
        requestService.doRequest("article/" + selectedArticleId, callback, "fromMenu", "true");


    }

}
