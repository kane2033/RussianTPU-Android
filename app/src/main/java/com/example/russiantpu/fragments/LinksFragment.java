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

import com.example.russiantpu.dataAdapters.DataAdapter;
import com.example.russiantpu.R;
import com.example.russiantpu.enums.ContentType;
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
public class LinksFragment extends Fragment {

    private List<LinkItem> getItemsById(int id) {
        /*
        тут должен быть get запрос на получение содержимого выбранного пункта,
        которое будет передано в фрагмент
        */
        List<LinkItem> itemsById = new ArrayList<>();
        switch (id) {
            case 1:
                itemsById.add(new LinkItem("Новости", 1, ContentType.FEED_LIST));
                itemsById.add(new LinkItem("Расписание", 2, ContentType.LINK));
                itemsById.add(new LinkItem("Личный кабинет", 3, ContentType.LINK));
                break;
            case 2:
                itemsById.add(new LinkItem("Новости", 4, ContentType.FEED_LIST));
                itemsById.add(new LinkItem("Советы", 5, ContentType.FEED_LIST));
                break;
        }
        return itemsById;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_links, container, false);
        final TextView textView = layoutInflater.findViewById(R.id.nav_links_text);
        //HTTP запрос (пока тестовый)
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
        });
        //вставка полученного результата из запроса

        int selectedItemId = getArguments().getInt("id", 1);
        List<LinkItem> items = getItemsById(selectedItemId);
        RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список

        //создаем адаптер
        DataAdapter adapter = new DataAdapter(this.getContext(), items);
        //устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
        return layoutInflater;
    }

}
