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

import com.example.russiantpu.dataAdapters.LinksDataAdapter;
import com.example.russiantpu.R;
import com.example.russiantpu.enums.ContentType;
import com.example.russiantpu.items.LinkItem;
import com.example.russiantpu.utility.FragmentReplacer;

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

    private List<LinkItem> items;
    private FragmentReplacer fragmentReplacer;

    private List<LinkItem> getItemsById(int id) {
        /*
        тут должен быть get запрос на получение содержимого выбранного пункта,
        которое будет передано в фрагмент
        */
        List<LinkItem> itemsById = new ArrayList<>();
        switch (id) {
            case 0:
                itemsById.add(new LinkItem("Новости", 0, ContentType.FEED_LIST));
                itemsById.add(new LinkItem("Расписание", 1, ContentType.LINK));
                itemsById.add(new LinkItem("Личный кабинет", 2, ContentType.LINK));
                break;
            case 1:
                itemsById.add(new LinkItem("Новости", 3, ContentType.FEED_LIST));
                itemsById.add(new LinkItem("Советы", 4, ContentType.FEED_LIST));
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

        fragmentReplacer = new FragmentReplacer(getFragmentManager());

        int selectedItemId = getArguments().getInt("id", 0);
        items = getItemsById(selectedItemId);
        RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список

        //создаем адаптер
        LinksDataAdapter adapter = new LinksDataAdapter(this.getContext(), items);
        //установка действия при клике
        adapter.setOnItemClickListener(new LinksDataAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                LinkItem clickedItem = items.get(position);
                fragmentReplacer.goToFragment(clickedItem.getType(), clickedItem.getId());
            }

            //пока не используется, оставлен на будущее
            @Override
            public void onItemLongClick(int position, View v) {
            }
        });
        //устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
        return layoutInflater;
    }

}
