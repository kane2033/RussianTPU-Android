package com.example.russiantpu.dataAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.russiantpu.R;
import com.example.russiantpu.items.FeedItem;

import java.util.List;

//DataAdapter для списка превью статей
public class FeedDataAdapter extends RecyclerView.Adapter<FeedDataAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<FeedItem> items;
    private static ClickListener clickListener;

    public FeedDataAdapter(Context context, List<FeedItem> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    //создание объекта ViewHolder, хранящего данные по одному пункту LinkItem
    @Override
    public FeedDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.feed_item, parent, false);
        return new ViewHolder(view);
    }

    //привязка объекта ViewHolder к объекту пункта LinkItem
    @Override
    public void onBindViewHolder(FeedDataAdapter.ViewHolder holder, int position) {
        FeedItem item = items.get(position);
        holder.header.setText(item.getHeader());
        holder.previewText.loadData(item.getPreviewText(), "text/html; charset=utf-8", "UTF-8");
        holder.date.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //класс, хранящий элементы управления
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView header;
        final ImageView image;
        final WebView previewText;
        final TextView date;

        ViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            header = view.findViewById(R.id.header);
            image = view.findViewById(R.id.image);
            previewText = view.findViewById(R.id.previewText);
            date = view.findViewById(R.id.date);
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
        FeedDataAdapter.clickListener = clickListener;
    }
}
