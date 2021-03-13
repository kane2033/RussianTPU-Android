package ru.tpu.russiantpu.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.main.dataAdapters.ClickListener;
import ru.tpu.russiantpu.main.dataAdapters.LinksDataAdapter;
import ru.tpu.russiantpu.main.items.LinkItem;
import ru.tpu.russiantpu.utility.FragmentReplacer;
import ru.tpu.russiantpu.utility.OnActivityRefreshRetained;

//фрагмент, отображающий список статей (новостей)
public class LinksFragment extends Fragment {

    private ArrayList<LinkItem> items;

    private final String itemsKey = "items";
    private String header = "";
    private final String headerKey = "header";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //восстанавливаем элементы из временной памяти
        // (пр.: смена ориентации)
        if (savedInstanceState != null) {
            items = savedInstanceState.getParcelableArrayList(itemsKey);
            header = savedInstanceState.getString(headerKey);
        } else { //иначе достаем список из args
            items = getArguments().getParcelableArrayList("children"); //получение пунктов из родительского фрагмента
            header = getArguments().getString("header"); //название выбранного пункта будет отображаться в тулбаре
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layoutInflater = (RelativeLayout) inflater.inflate(R.layout.fragment_links, container, false);
        TextView contentMissingText = layoutInflater.findViewById(R.id.missingContentText);
        RecyclerView recyclerView = layoutInflater.findViewById(R.id.list); //список
        final FragmentReplacer fragmentReplacer = new FragmentReplacer((AppCompatActivity) getActivity());

        // При входе в фрагмент указываем, что при свайпе вверх используется
        // refreshListener, который находится в активити
        OnActivityRefreshRetained activity = (OnActivityRefreshRetained) getActivity();
        if (activity != null) {
            activity.retainActivityRefresh();
        }


        if (!header.isEmpty()) {
            getActivity().setTitle(header); //установка названия пункта в тулбар
        }

        //создаем адаптер
        final LinksDataAdapter adapter = new LinksDataAdapter(getContext(), items);
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

        if (items == null) { //если нет контента, уведомляем
            contentMissingText.setText(R.string.missing_content);
        }

        return layoutInflater;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelableArrayList(itemsKey, items);
        bundle.putString(headerKey, header);
    }
}
