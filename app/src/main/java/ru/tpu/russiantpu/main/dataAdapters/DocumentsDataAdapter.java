package ru.tpu.russiantpu.main.dataAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.dto.DocumentDTO;

//DataAdapter для списка превью статей
public class DocumentsDataAdapter extends RecyclerView.Adapter<DocumentsDataAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<DocumentDTO> items;
    private static ClickListener clickListener;

    public DocumentsDataAdapter(Context context, List<DocumentDTO> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    //создание объекта ViewHolder, хранящего данные по одному пункту DocumentDTO
    @NonNull
    @Override
    public DocumentsDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_document, parent, false);
        return new ViewHolder(view);
    }

    //привязка объекта ViewHolder к объекту пункта LinkItem
    @Override
    public void onBindViewHolder(DocumentsDataAdapter.ViewHolder holder, int position) {
        DocumentDTO item = items.get(position);

        String name = "\"" + item.getName() + "\""; //выводим название в кавычках
        holder.name.setText(name);
        holder.loadDate.setText(item.getLoadDate());
        holder.fileName.setText(item.getFileName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //класс, хранящий элементы управления
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView name;
        final TextView loadDate;
        final View downloadView;
        final TextView fileName;

        ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.doc_name);
            loadDate = view.findViewById(R.id.doc_date);
            downloadView = view.findViewById(R.id.doc_download);
            fileName = downloadView.findViewById(R.id.doc_file_name);
            //устанавливаем клик на контейнер элементов загрузки
            //(название файла и иконка)
            downloadView.setOnClickListener(this);
            downloadView.setOnLongClickListener(this);
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
        DocumentsDataAdapter.clickListener = clickListener;
    }
}
