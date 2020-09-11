package ru.tpu.russiantpu.dataAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.items.LinkItem;
import com.squareup.picasso.Picasso;

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
        if (item.getImage() != null) { //если есть картинка
            holder.image.setVisibility(View.VISIBLE); //делаем view видимым
            //устанавливаем картинку из url в imageView форматом Bitmap
            Picasso.get().load(item.getImage()).into(holder.image);
        }
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
