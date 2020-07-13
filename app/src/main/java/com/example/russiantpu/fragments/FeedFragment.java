package com.example.russiantpu.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.russiantpu.dataAdapters.ClickListener;
import com.example.russiantpu.dataAdapters.FeedDataAdapter;
import com.example.russiantpu.R;
import com.example.russiantpu.items.FeedItem;
import com.example.russiantpu.utility.FragmentReplacer;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;

import java.util.ArrayList;
import java.util.List;

//фрагмент, отображающий список статей (новостей)
public class FeedFragment extends Fragment {

    private ArrayList<FeedItem> items = new ArrayList<>();
    private FeedDataAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = layoutInflater.findViewById(R.id.list); //список
        /*//создаем адаптер
        adapter = new FeedDataAdapter(this.getContext(), items);
        //устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);*/
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
        //реализация коллбека - что произойдет при получении данных с сервера
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                items = gsonService.fromJsonToArrayList(jsonBody, FeedItem.class);
                Log.d("FEED_FRAGMENT", "Сколько статей получено: " + items.size());

                //отрисовываем список статей в потоке интерфейса
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //уведомление адаптера об обновлении данных
                        //adapter.notifyDataSetChanged();
                        adapter = new FeedDataAdapter(getContext(), items);
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
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        };
        //запрос за получение списка статей по айди пункта меню
        requestService.doRequest("article/list/" + selectedItemId, callback, "fromMenu", "true");
    }

}
