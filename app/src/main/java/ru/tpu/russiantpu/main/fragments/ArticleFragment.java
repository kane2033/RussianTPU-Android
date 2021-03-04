package ru.tpu.russiantpu.main.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.main.items.Article;
import ru.tpu.russiantpu.utility.ArticleUrlParser;
import ru.tpu.russiantpu.utility.ChromeClient;
import ru.tpu.russiantpu.utility.FragmentReplacer;
import ru.tpu.russiantpu.utility.MainActivityItems;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.StartActivityService;
import ru.tpu.russiantpu.utility.ToastService;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;

//фрагмент, отображающий список статей (новостей)
public class ArticleFragment extends Fragment {

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
        missingContentText = layoutInflater.findViewById(R.id.missingContentText);
        frameLayout = layoutInflater.findViewById(R.id.fullscreen_container); //контейнер для полноэкранного режима
        progressBar = getActivity().findViewById(R.id.progress_bar);

        return layoutInflater;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WebView webView = getView().findViewById(R.id.fullArticle); //статья
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean isUrlValid = URLUtil.isValidUrl(url);
                if (isUrlValid) {
                    // Если ссылка валидна,
                    // она открывается в webview
                    view.loadUrl(url);
                    return false;
                } else {
                    // Если ссылка не валидна
                    progressBar.show();
                    openDeepLink(url);
                    return true;
                }
            }
        });

        // Возвращаемся на предыдущую ссылку вместо закрытия экрана
        webView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                        return true;
                    }
                }
            }
            return false;
        });

        //восстанавливаем элементы из временной памяти
        // (пр.: смена ориентации)
        if (savedInstanceState != null && savedInstanceState.getParcelable(articleKey) != null) {
            article = savedInstanceState.getParcelable(articleKey);
            showArticle(webView);
        } else { //иначе делаем запрос на сервис
            //вспомогательные классы
            final SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(requireActivity());
            final GsonService gsonService = new GsonService();
            final ToastService toastService = new ToastService(getContext());
            requestService = new RequestService(sharedPreferencesService, new StartActivityService(requireActivity()));

            String selectedArticleId = getArguments().getString("id");

            progressBar.show(); //включаем прогресс бар

            //реализация коллбека - что произойдет при получении данных с сервера
            GenericCallback<String> callback = new GenericCallback<String>() {
                @Override
                public void onResponse(String jsonBody) {
                    article = gsonService.fromJsonToObject(jsonBody, Article.class);
                    requireActivity().runOnUiThread(() -> {
                        if (article.getTopic() == null) { //если нет контента, уведомляем
                            missingContentText.setText(R.string.missing_content);
                        } else { //иначе заполняем фрагмент содержимым статьи
                            showArticle(webView);
                        }
                        progressBar.hide();
                    });
                }

                @Override
                public void onError(String message) {
                    //выключаем прогресс бар
                    requireActivity().runOnUiThread(() -> progressBar.hide());
                    toastService.showToast(message);
                }

                @Override
                public void onFailure(String message) {
                    //выключаем прогресс бар
                    requireActivity().runOnUiThread((Runnable) () -> progressBar.hide());
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
    private void showArticle(WebView webView) {
        //настройки для отображения видео
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new ChromeClient(webView, frameLayout));
        //отображение в формате html
        webView.loadDataWithBaseURL("https://internationals.tpu.ru:8080",
                article.getText(), "text/html", "UTF-8", null);
    }

    // Асинхронно парсим диплинк и открываем соответсвующий фрагмент, если парсинг успешен
    private void openDeepLink(String url) {
        ArticleUrlParser.INSTANCE.navigateDeepLink(url, (MainActivityItems) getActivity(), item -> {
            if (item != null) {
                FragmentReplacer fragmentReplacer =
                        new FragmentReplacer((AppCompatActivity) requireActivity());
                fragmentReplacer.goToFragment(item);
            } else {
                // Показываем ошибку, если это не веб ссылка и не диплинк
                Toast.makeText(getContext(), R.string.link_open_error, Toast.LENGTH_SHORT).show();
            }
            progressBar.hide();
            return null;
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(articleKey, article);
    }

    /*
     * Прячем тулбар в onResume и onStop
     * */
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //при закрытии фрагмента отменяем все запросы
        if (requestService != null) {
            requestService.cancelAllRequests();
        }
        if (progressBar != null) {
            //выключаем прогрессбар
            progressBar.hide();
        }
    }
}
