package com.example.russiantpu.fragments;

import android.os.Bundle;
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
import com.example.russiantpu.dataAdapters.LinksDataAdapter;
import com.example.russiantpu.items.LinkItem;
import com.example.russiantpu.utility.FragmentReplacer;

import java.util.ArrayList;

//фрагмент, отображающий список статей (новостей)
public class LinksFragment extends Fragment {

    private TextView contentMissingText;
    private LinksDataAdapter adapter;
    private FragmentReplacer fragmentReplacer;
    private ArrayList<LinkItem> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_links, container, false);
        contentMissingText = layoutInflater.findViewById(R.id.missingContentText);
        RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список
        fragmentReplacer = new FragmentReplacer((AppCompatActivity) getActivity());

        items = getArguments().getParcelableArrayList("children"); //получение пунктов из родительского фрагмента
        String header = getArguments().getString("header"); //название выбранного пункта будет отображаться в тулбаре
        getActivity().setTitle(header); //установка названия пункта в тулбар

        if (items == null) { //если нет контента, уведомляем
            contentMissingText.setText(R.string.missing_content);
        }
        else { //иначе заполняем recycleview
            //создаем адаптер
            adapter = new LinksDataAdapter(this.getContext(), items);
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
        }
        return layoutInflater;
    }



}
