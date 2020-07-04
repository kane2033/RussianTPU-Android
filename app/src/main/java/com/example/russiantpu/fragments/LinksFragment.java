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

import com.example.russiantpu.dataAdapters.ClickListener;
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

    private List<LinkItem> getItemsById(String id) {
        /*
        тут должен быть get запрос на получение содержимого выбранного пункта,
        которое будет передано в фрагмент
        */
        List<LinkItem> itemsById = new ArrayList<>();
        switch (id) {
            case "144db080-3223-4449-b49f-45f392af6552":
                itemsById.add(new LinkItem("Новости", "stringId1",1, ContentType.FeedList));
                itemsById.add(new LinkItem("Расписание", "stringId2", 2, ContentType.Link));
                itemsById.add(new LinkItem("Личный кабинет", "stringId3", 3, ContentType.Link));
                break;
            case "b9ac6dc4-9b72-4877-8714-b93e38d803aa":
                itemsById.add(new LinkItem("Новости", "stringId4", 4, ContentType.FeedList));
                itemsById.add(new LinkItem("Советы", "stringId5", 5, ContentType.FeedList));
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

        String selectedItemId = getArguments().getString("id", "stringId1");
        items = getItemsById(selectedItemId);
        RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список

        //создаем адаптер
        LinksDataAdapter adapter = new LinksDataAdapter(this.getContext(), items);
        //установка действия при клике
        adapter.setOnItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                LinkItem selectedItem = items.get(position);
                fragmentReplacer.goToFragment(selectedItem);
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
