package ru.tpu.russiantpu.main.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    private Article article;
    private final String articleKey = "article";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //отображаем в фрагменте
        View layoutInflater = inflater.inflate(R.layout.fragment_article, container, false);
        webView = layoutInflater.findViewById(R.id.fullArticle); //статья
        missingContentText = layoutInflater.findViewById(R.id.missingContentText);
        frameLayout = layoutInflater.findViewById(R.id.fullscreen_container); //контейнер для полноэкранного режима
        progressBar = getActivity().findViewById(R.id.progress_bar);
        swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh);

        return layoutInflater;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    // Если ссылка валидна, открывается в браузере
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else {
                    // Если ссылка не валидна
                    // пробуем открыть диплинк
                    progressBar.show();
                    openDeepLink(url);
                }
                return true;
            }

            // Отключаем скачивание картинки сайта, которой нет (засоряет лог сервиса)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (!request.isForMainFrame() && request.getUrl().getPath().endsWith("/favicon.ico")) {
                    try {
                        return new WebResourceResponse("image/png", null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
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
            final SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(getActivity());
            final GsonService gsonService = new GsonService();
            final ToastService toastService = new ToastService(getContext());
            requestService = new RequestService(sharedPreferencesService, new StartActivityService(getActivity()));

            //получение JWT токена
            final String token = sharedPreferencesService.getToken();
            final String language = sharedPreferencesService.getLanguageId();

            getArticle(token, language, webView, gsonService, toastService);

            // Также делаем все запросы повторно при свайпе вверх
            swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                getArticle(token, language, webView, gsonService, toastService);
                swipeRefreshLayout.setRefreshing(false);
            });
        }
    }

    private void getArticle(String token, String language, WebView webView,
                            GsonService gsonService, ToastService toastService) {
        progressBar.show(); //включаем прогресс бар
        //реализация коллбека - что произойдет при получении данных с сервера
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                article = gsonService.fromJsonToObject(jsonBody, Article.class);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (article.getTopic() == null) { //если нет контента, уведомляем
                            missingContentText.setText(R.string.missing_content);
                        } else { //иначе заполняем фрагмент содержимым статьи
                            showArticle(webView);
                        }
                        progressBar.hide();
                    });
                }
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> progressBar.hide());
                }
                toastService.showToast(message);
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> progressBar.hide());
                }
                toastService.showToast(R.string.article_error);
            }
        };

        //запрос за получение списка статей по айди пункта меню
        final String selectedArticleId = getArguments().getString("id");
        if (selectedArticleId == null) {
            requestService.doRequest("article", callback, token, language);
        } else {
            requestService.doRequest("article", callback, token, language,
                    "id", selectedArticleId);
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
                // Открываем соответствующий фрагмент, если парсинг успешен
                FragmentReplacer fragmentReplacer =
                        new FragmentReplacer((AppCompatActivity) getActivity());
                fragmentReplacer.goToFragment(item);
            } else {
                try {
                    // Пробуем открыть в качестве иной ссылки (позвонить, открыть почтовой клиент и др.)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception e) {
                    // Показываем ошибку, если это не веб ссылка и не диплинк
                    Toast.makeText(getContext(), R.string.link_open_error, Toast.LENGTH_SHORT).show();
                }
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

    // Фикс бага, при котором нельзя скроллить вверх,
    // когда достигли самого низа webview и есть swipeRefreshLayout
    @Override
    public void onStart() {
        super.onStart();
        swipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener =
                () -> swipeRefreshLayout.setEnabled(webView.getScrollY() == 0));
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
        swipeRefreshLayout.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
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
