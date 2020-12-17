package ru.tpu.russiantpu.main.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.main.items.Article;
import ru.tpu.russiantpu.utility.ChromeClient;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.StartActivityService;
import ru.tpu.russiantpu.utility.ToastService;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;

//фрагмент, отображающий список статей (новостей)
public class ArticleFragment extends Fragment {

    private WebView webView;
    private TextView missingContentText;
    private FrameLayout frameLayout;
    private ContentLoadingProgressBar progressBar;
    private RequestService requestService;

    private Article article;
    private final String articleKey = "article";

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
        final Activity activity = getActivity();

        //восстанавливаем элементы из временной памяти
        // (пр.: смена ориентации)
        if (savedInstanceState != null && savedInstanceState.getParcelable(articleKey) != null) {
            article = savedInstanceState.getParcelable(articleKey);
            showArticle(activity);
        }
        else { //иначе делаем запрос на сервис
            //вспомогательные классы
            final SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(activity);
            final GsonService gsonService = new GsonService();
            final ToastService toastService = new ToastService(getContext());
            requestService = new RequestService(sharedPreferencesService, new StartActivityService(activity));

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
                            if (article.getTopic() == null) { //если нет контента, уведомляем
                                missingContentText.setText(R.string.missing_content);
                            }
                            else { //иначе заполняем фрагмент содержимым статьи
                                showArticle(activity);
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
                    toastService.showToast(message);
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
                    toastService.showToast(R.string.article_error);
                }
            };

            //получение JWT токена
            String token = sharedPreferencesService.getToken();
            String language = sharedPreferencesService.getLanguageId();
            //запрос за получение списка статей по айди пункта меню
            requestService.doRequest("article/" + selectedArticleId, callback, token, language);
        }
    }

    //метод выводит полученную статью на экран
    private void showArticle(Activity activity) {
        //установка заголовка в тулбаре
        activity.setTitle(article.getTopic());
        //настройки для отображения видео
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new ChromeClient(webView, frameLayout));
        //отображение в формате html
        webView.loadData(article.getText(), "text/html; charset=utf-8", "UTF-8");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(articleKey, article);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //при закрытии фрагмента отменяем все запросы
        if (requestService != null ) {
            requestService.cancelAllRequests();
        }
        if (progressBar != null) {
            //выключаем прогрессбар
            progressBar.hide();
        }
    }
}
