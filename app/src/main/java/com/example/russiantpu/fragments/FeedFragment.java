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
import com.example.russiantpu.dataAdapters.FeedDataAdapter;
import com.example.russiantpu.dataAdapters.LinksDataAdapter;
import com.example.russiantpu.R;
import com.example.russiantpu.enums.ContentType;
import com.example.russiantpu.items.FeedItem;
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
public class FeedFragment extends Fragment {

    private List<FeedItem> getItemsById(int id) {

        /*тут должен быть get запрос на получение содержимого выбранного пункта,
        которое будет передано в фрагмент*/

        List<FeedItem> itemsById = new ArrayList<>();
        switch (id) {
            case 0:
                itemsById.add(new FeedItem(0, "Заголовок статьи 1", "<h1>Первые ~50 слов</h1>", "22.02.2020"));
                itemsById.add(new FeedItem(1, "Заголовок статьи 2", "Первые ~50 слов", "20.02.2020"));
                itemsById.add(new FeedItem(2, "Заголовок статьи 3", "Первые ~50 слов", "18.02.2020"));
                itemsById.add(new FeedItem(3, "Заголовок статьи 4", "Первые ~50 слов", "05.02.2020"));
                break;
            case 3:
                itemsById.add(new FeedItem(4, "Заголовок статьи 5", "Первые ~50 слов", "21.02.2020"));
                itemsById.add(new FeedItem(5, "Заголовок статьи 6", "Первые ~50 слов", "19.02.2020"));
                itemsById.add(new FeedItem(6, "Заголовок статьи 7", "Первые ~50 слов", "01.02.2020"));
                break;
        }
        return itemsById;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_feed, container, false);

        int selectedItemId = getArguments().getInt("id", 1);
        final List<FeedItem> items = getItemsById(selectedItemId);
        final RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список
        final FragmentReplacer fragmentReplacer = new FragmentReplacer(getFragmentManager());

        //создаем адаптер
        final FeedDataAdapter adapter = new FeedDataAdapter(this.getContext(), items);
        adapter.setOnItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                FeedItem selectedItem = items.get(position);
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
