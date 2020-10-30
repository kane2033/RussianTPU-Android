package ru.tpu.russiantpu.main.dataAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.main.items.FeedItem;
import ru.tpu.russiantpu.utility.ImageConverter;

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
        View view = inflater.inflate(R.layout.item_feed, parent, false);
        return new ViewHolder(view);
    }

    //привязка объекта ViewHolder к объекту пункта LinkItem
    @Override
    public void onBindViewHolder(FeedDataAdapter.ViewHolder holder, int position) {
        FeedItem item = items.get(position);

        //конвертация строки base64 в картинку
        ImageConverter imageConverter = new ImageConverter();
        if (item.getArticleImage() != null) { //если есть картинка
            Bitmap imgBitmap = imageConverter.stringToBitmap(item.getArticleImage());
            holder.image.setVisibility(View.VISIBLE); //делаем view видимым
            holder.image.setImageBitmap(imgBitmap);
        }

        holder.header.setText(item.getTopic());
        holder.previewText.setText(item.getBriefText());
        holder.date.setText(item.getCreateDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //класс, хранящий элементы управления
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView header;
        final ImageView image;
        final TextView previewText;
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
