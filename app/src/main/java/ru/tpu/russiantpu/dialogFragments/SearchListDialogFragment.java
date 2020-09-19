package ru.tpu.russiantpu.dialogFragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.dialogFragments.dataAdapters.SearchListDialogDataAdapter;
import ru.tpu.russiantpu.main.dataAdapters.ClickListener;
import ru.tpu.russiantpu.utility.callbacks.ListDialogCallback;

public class SearchListDialogFragment extends DialogFragment {

    private final ListDialogCallback callback; //интерфейс содержит действия при нажатии на элемент списка
    private final int layoutResId; //Id of layout
    private final List<String> list; //отображаемый список

    public SearchListDialogFragment(ListDialogCallback callback, int layoutResId, List<String> list) {
        this.callback = callback;
        this.layoutResId = layoutResId;
        this.list = list;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(layoutResId, null);
        builder.setView(view);
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss(); //закрываем диалог
                    }});

        //настройка адаптера списка
        final RecyclerView recyclerView = view.findViewById(R.id.list); //список
        final SearchListDialogDataAdapter adapter = new SearchListDialogDataAdapter(getContext(), list);
        adapter.setOnItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                callback.onItemClick(list.get(position)); //возвращаем через коллбэк выбранный предмет
                getDialog().dismiss();
            }
            //пока не используется, оставлен на будущее
            @Override
            public void onItemLongClick(int position, View v) {
            }
        });
        recyclerView.setAdapter(adapter);

        //поле поиска
        final EditText searchInput = view.findViewById(R.id.list_search);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterList(editable.toString(), adapter);
            }
        });

        return builder.create();
    }

    private void filterList(String text, SearchListDialogDataAdapter adapter) {
        List<String> filteredList = new ArrayList<>();
        for (String item : list) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterItems(filteredList); //фильтруем по заданному тексту наш список
    }
}
