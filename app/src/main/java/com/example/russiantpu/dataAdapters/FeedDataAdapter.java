package com.example.russiantpu.dataAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.russiantpu.R;
import com.example.russiantpu.items.FeedItem;
import com.example.russiantpu.items.LinkItem;

import java.util.List;

//DataAdapter для списка превью статей
public class FeedDataAdapter extends RecyclerView.Adapter<FeedDataAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<FeedItem> items;

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
        holder.image.setImageDrawable(item.getImage());
        holder.previewText.setText(item.getPreviewText());
        holder.date.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //класс, хранящий элементы управления
    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView header;
        final ImageView image;
        final TextView previewText;
        final TextView date;
        ViewHolder(View view){
            super(view);
            header = view.findViewById(R.id.header);
            image = view.findViewById(R.id.image);
            previewText = view.findViewById(R.id.previewText);
            date = view.findViewById(R.id.date);
        }
    }
}
