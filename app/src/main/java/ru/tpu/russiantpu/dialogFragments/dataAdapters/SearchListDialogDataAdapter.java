package ru.tpu.russiantpu.dialogFragments.dataAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.main.dataAdapters.ClickListener;

/**
 * DataAdapter простого списка, появляющегося в DialogFragment
 * */
public class SearchListDialogDataAdapter extends RecyclerView.Adapter<SearchListDialogDataAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<String> items;
    private static ClickListener clickListener;

    public SearchListDialogDataAdapter(Context context, List<String> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    //создание объекта ViewHolder, хранящего данные по одному пункту LinkItem
    @NonNull
    @Override
    public SearchListDialogDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_search_list, parent, false);
        return new SearchListDialogDataAdapter.ViewHolder(view);
    }

    //привязка объекта ViewHolder к объекту пункта LinkItem
    @Override
    public void onBindViewHolder(final SearchListDialogDataAdapter.ViewHolder holder, int position) {
        holder.itemText.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //заменяем имеющийся список отфильтрованным
    public void filterItems(List<String> filteredList) {
        items = filteredList;
        notifyDataSetChanged();
    }

    //класс, хранящий элементы управления
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView itemText;

        ViewHolder(View view){
            super(view);
            itemText = view.findViewById(R.id.name);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        SearchListDialogDataAdapter.clickListener = clickListener;
    }
}
