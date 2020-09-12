package ru.tpu.russiantpu.main.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.main.dataAdapters.ClickListener;
import ru.tpu.russiantpu.main.dataAdapters.FeedDataAdapter;
import ru.tpu.russiantpu.main.enums.ContentType;
import ru.tpu.russiantpu.main.items.FeedItem;
import ru.tpu.russiantpu.main.items.Item;
import ru.tpu.russiantpu.utility.FragmentReplacer;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.StartActivityService;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.dialogFragmentServices.ErrorDialogService;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;

//фрагмент, отображающий список статей (новостей)
public class FeedFragment extends Fragment {

    private RequestService requestService;
    private ContentLoadingProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        final FragmentManager fragmentManager = getFragmentManager();

        final RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_feed, container, false);
        final RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список
        final TextView missingContentText = layoutInflater.findViewById(R.id.missingContentText); //уведомление об отутствии контента
        progressBar = activity.findViewById(R.id.progress_bar);

        //вспомогательные классы
        final SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(activity);
        final FragmentReplacer fragmentReplacer = new FragmentReplacer((AppCompatActivity) activity);
        final GsonService gsonService = new GsonService();
        requestService = new RequestService(sharedPreferencesService, new StartActivityService(activity));

        final ArrayList<FeedItem> items = new ArrayList<>();
        final FeedDataAdapter adapter = new FeedDataAdapter(getContext(), items);

        adapter.setOnItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Item selectedItem = items.get(position);
                selectedItem.setType(ContentType.ARTICLE);
                fragmentReplacer.goToFragment(selectedItem);
            }
            //пока не используется, оставлен на будущее
            @Override
            public void onItemLongClick(int position, View v) {
            }
        });
        recyclerView.setAdapter(adapter);

        String selectedItemId = null; //айди родительского пункта
        if (getArguments() != null) {
            selectedItemId = getArguments().getString("id");
            String header = getArguments().getString("header"); //название выбранного пункта будет отображаться в тулбаре
            activity.setTitle(header); //установка названия пункта в тулбар
        }


        progressBar.show(); //включаем прогресс бар

        //реализация коллбека - что произойдет при получении данных с сервера
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                items.addAll(gsonService.fromJsonToArrayList(jsonBody, FeedItem.class));

                //отрисовываем список статей в потоке интерфейса
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (items.size() != 0) { //если нет контента, уведомляем
                            Log.d("FEED_FRAGMENT", "Сколько статей получено: " + items.size());
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            missingContentText.setText(R.string.missing_content);
                        }
                        progressBar.hide(); //выключаем прогресс бар
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
                ErrorDialogService.showDialog(getResources().getString(R.string.feed_error), message, fragmentManager);
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
                ErrorDialogService.showDialog(getResources().getString(R.string.feed_error), message, fragmentManager);
            }
        };

        //получение JWT токена
        String token = sharedPreferencesService.getToken();
        String language = sharedPreferencesService.getLanguage();

        //запрос за получение списка статей по айди пункта меню
        requestService.doRequest("article/list/" + selectedItemId, callback, token, language, "fromMenu", "true");

        return layoutInflater;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
