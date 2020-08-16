package com.example.russiantpu.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.russiantpu.R;
import com.example.russiantpu.dataAdapters.ClickListener;
import com.example.russiantpu.dataAdapters.FeedDataAdapter;
import com.example.russiantpu.enums.ContentType;
import com.example.russiantpu.items.FeedItem;
import com.example.russiantpu.items.Item;
import com.example.russiantpu.utility.ErrorDialogService;
import com.example.russiantpu.utility.FragmentReplacer;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;
import com.example.russiantpu.utility.SharedPreferencesService;

import java.util.ArrayList;

//фрагмент, отображающий список статей (новостей)
public class FeedFragment extends Fragment {

    private FeedDataAdapter adapter;
    private RecyclerView recyclerView;
    private TextView missingContentText;

    private ArrayList<FeedItem> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = layoutInflater.findViewById(R.id.list); //список
        missingContentText = layoutInflater.findViewById(R.id.missingContentText); //уведомление об отутствии контента
        return layoutInflater;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //вспомогательные классы
        final Activity activity = getActivity();
        final FragmentReplacer fragmentReplacer = new FragmentReplacer((AppCompatActivity) activity);
        final RequestService requestService = new RequestService();
        final GsonService gsonService = new GsonService();

        String selectedItemId = getArguments().getString("id"); //айди родительского пункта
        String header = getArguments().getString("header"); //название выбранного пункта будет отображаться в тулбаре
        activity.setTitle(header); //установка названия пункта в тулбар
        //реализация коллбека - что произойдет при получении данных с сервера
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                items = gsonService.fromJsonToArrayList(jsonBody, FeedItem.class);

                //иначе заполняем recycleview
                //отрисовываем список статей в потоке интерфейса
                //возможно, это стоит перенести в onCreateView
                //и при успешном запросе вызывать обновление recycleview
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (items.size() == 0) { //если нет контента, уведомляем
                            missingContentText.setText(R.string.missing_content);
                        }
                        else {
                            Log.d("FEED_FRAGMENT", "Сколько статей получено: " + items.size());
                            adapter = new FeedDataAdapter(getContext(), items);
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
                        }
                    }

                });
            }

            @Override
            public void onError(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.feed_error), gsonService.getFieldFromJson("message", message), getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.feed_error), message, getFragmentManager());
            }
        };
        //получение JWT токена
        SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(activity);
        String token = sharedPreferencesService.getToken();
        String language = sharedPreferencesService.getLanguage();

        //запрос за получение списка статей по айди пункта меню
        requestService.doRequest("article/list/" + selectedItemId, callback, token, language, "fromMenu", "true");
    }

}
