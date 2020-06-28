package com.example.russiantpu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.russiantpu.dataAdapters.FeedDataAdapter;
import com.example.russiantpu.dataAdapters.LinksDataAdapter;
import com.example.russiantpu.R;
import com.example.russiantpu.enums.ContentType;
import com.example.russiantpu.items.FeedItem;
import com.example.russiantpu.items.LinkItem;

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
public class FeedFragment extends Fragment {

    private List<FeedItem> getItemsById(int id) {

        /*тут должен быть get запрос на получение содержимого выбранного пункта,
        которое будет передано в фрагмент*/

        List<FeedItem> itemsById = new ArrayList<>();
        switch (id) {
            case 0:
                itemsById.add(new FeedItem(0, "Заголовок статьи 1", null, "Первые ~50 слов", "22.02.2020"));
                itemsById.add(new FeedItem(1, "Заголовок статьи 2", null, "Первые ~50 слов", "20.02.2020"));
                itemsById.add(new FeedItem(2, "Заголовок статьи 3", null, "Первые ~50 слов", "18.02.2020"));
                itemsById.add(new FeedItem(3, "Заголовок статьи 4", null, "Первые ~50 слов", "05.02.2020"));
                break;
            case 3:
                itemsById.add(new FeedItem(4, "Заголовок статьи 5", null, "Первые ~50 слов", "21.02.2020"));
                itemsById.add(new FeedItem(5, "Заголовок статьи 6", null, "Первые ~50 слов", "19.02.2020"));
                itemsById.add(new FeedItem(6, "Заголовок статьи 7", null, "Первые ~50 слов", "01.02.2020"));
                break;
        }
        return itemsById;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_feed, container, false);
        final TextView textView = layoutInflater.findViewById(R.id.nav_feed_text);
        /*//HTTP запрос (пока тестовый)
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
        });*/

        int selectedItemId = getArguments().getInt("id", 1);
        List<FeedItem> items = getItemsById(selectedItemId);
        RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список

        //создаем адаптер
        FeedDataAdapter adapter = new FeedDataAdapter(this.getContext(), items);
        //устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
        return layoutInflater;
    }

}