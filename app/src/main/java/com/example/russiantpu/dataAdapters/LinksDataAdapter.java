package com.example.russiantpu.dataAdapters;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

import com.example.russiantpu.R;
import com.example.russiantpu.items.LinkItem;

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
    public void onBindViewHolder(LinksDataAdapter.ViewHolder holder, int position) {
        LinkItem item = items.get(position);
        String text = item.getName() + "; id = " + item.getId();
        holder.nameText.setText(text);
        //holder.companyView.setText(item.getCompany());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    //класс, хранящий элементы управления
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView nameText;
        final Button button;

        ViewHolder(View view){
            super(view);
            nameText = view.findViewById(R.id.name);
            button = view.findViewById(R.id.link_button);
            button.setOnClickListener(this);
            button.setOnLongClickListener(this);
            //companyView = (TextView) view.findViewById(R.id.company);
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

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

}