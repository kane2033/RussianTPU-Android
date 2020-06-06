package com.example.russiantpu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.russiantpu.DataAdapter;
import com.example.russiantpu.R;
import com.example.russiantpu.utility.LinkItem;

import java.util.ArrayList;
import java.util.List;

//фрагмент, отображающий список статей (новостей)
public class LinksFragment extends Fragment {

    private TextView textView;
    private String message;
    private int selectedItemId;

    private List<LinkItem> items = new ArrayList<>();

    private List<LinkItem> getItemsById(int id) {
        /*
        тут должен быть get запрос на получение содержимого выбранного пункта,
        которое будет передано в фрагмент
        */
        List<LinkItem> itemsById = new ArrayList<>();
        switch (id) {
            case 1:
                itemsById.add(new LinkItem("Новости", 1, false));
                itemsById.add(new LinkItem("Расписание", 2, true));
                itemsById.add(new LinkItem("Личный кабинет", 3, true));
                break;
            case 2:
                itemsById.add(new LinkItem("Новости", 4, false));
                itemsById.add(new LinkItem("Советы", 5, false));
                break;
        }
        return itemsById;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_links, container, false);
        //textView = layoutInflater.findViewById(R.id.nav_links_text);
        //message = getArguments().getString("linksMessage", "null");
        //textView.setText(message);
        selectedItemId = getArguments().getInt("id", 1);

        items = getItemsById(selectedItemId);

        RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список
        //создаем адаптер
        DataAdapter adapter = new DataAdapter(this.getContext(), items);
        //устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
        return layoutInflater;
    }

}
