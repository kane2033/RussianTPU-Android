package com.example.russiantpu.dataAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.russiantpu.R;
import com.example.russiantpu.items.LinkItem;
import com.example.russiantpu.utility.ImageConverter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

//DataAdapter для списка ссылок
public class LinksDataAdapter extends RecyclerView.Adapter<LinksDataAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<LinkItem> items;
    private static ClickListener clickListener;

    public LinksDataAdapter(Context context, List<LinkItem> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    //создание объекта ViewHolder, хранящего данные по одному пункту LinkItem
    @Override
    public LinksDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    //привязка объекта ViewHolder к объекту пункта LinkItem
    @Override
    public void onBindViewHolder(final LinksDataAdapter.ViewHolder holder, int position) {
        LinkItem item = items.get(position);
        String text = item.getName();
        holder.nameText.setText(text);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.image.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                holder.image.setImageDrawable(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                holder.image.setImageDrawable(placeHolderDrawable);
            }
        };

        //устанавливаем картинку из url в imageView форматом Bitmap
        Picasso.get().load(item.getImage()).into(target);

/*        //конвертация строки base64 в картинку
        ImageConverter imageConverter = new ImageConverter();
        Bitmap imgBitmap = imageConverter.stringToBitmap(item.getImage());
        holder.image.setImageBitmap(imgBitmap);*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    //класс, хранящий элементы управления
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView nameText;
        final ImageView image;

        ViewHolder(View view){
            super(view);
            nameText = view.findViewById(R.id.name);
            image = view.findViewById(R.id.image);
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
        LinksDataAdapter.clickListener = clickListener;
    }

}
