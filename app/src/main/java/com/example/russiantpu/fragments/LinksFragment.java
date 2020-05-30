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

    List<LinkItem> items = new ArrayList<>();

    

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_links, container, false);
        textView = layoutInflater.findViewById(R.id.nav_links_text);
        //message = getArguments().getString("linksMessage", "null");
        //textView.setText(message);
        selectedItemId = getArguments().getInt("id", 1);

        RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список
        //создаем адаптер
        DataAdapter adapter = new DataAdapter(this.getContext(), items);
        //устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
        return layoutInflater;
    }

}
