package ru.tpu.russiantpu.utility.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import ru.tpu.russiantpu.dto.LanguageDTO;

public class LanguagesAdapter extends ArrayAdapter<LanguageDTO> {
    
    private final List<LanguageDTO> languages;

    public LanguagesAdapter(Context context, List<LanguageDTO> languages) {
        super(context, android.R.layout.simple_spinner_item, languages);
        this.languages = languages;
    }

    @Override
    public int getCount(){
        return languages.size();
    }

    @Override
    public LanguageDTO getItem(int position){
        return languages.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        //label.setPadding(label.getPaddingLeft(), 0, label.getPaddingRight(), 0);
        label.setText(languages.get(position).getFullName()); //отображем полное название языка
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        //label.setPadding(label.getPaddingLeft(), 4, label.getPaddingRight(), 4);
        label.setText(languages.get(position).getFullName());
        return label;
    }

}
