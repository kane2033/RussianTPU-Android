package com.example.russiantpu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.russiantpu.dataAdapters.ClickListener;
import com.example.russiantpu.dataAdapters.LinksDataAdapter;
import com.example.russiantpu.R;
import com.example.russiantpu.items.LinkItem;
import com.example.russiantpu.utility.FragmentReplacer;

import java.util.ArrayList;

//фрагмент, отображающий список статей (новостей)
public class LinksFragment extends Fragment {

    private ArrayList<LinkItem> items;
    private FragmentReplacer fragmentReplacer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layoutInflater = (RelativeLayout)inflater.inflate(R.layout.fragment_links, container, false);
        fragmentReplacer = new FragmentReplacer(getFragmentManager());

        /*String selectedItemId = getArguments().getString("id", "stringId1");
        items = getItemsById(selectedItemId); получение пунктов без запросов*/

        //получение пунктов из родительского фрагмента
        items = getArguments().getParcelableArrayList("children");

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
