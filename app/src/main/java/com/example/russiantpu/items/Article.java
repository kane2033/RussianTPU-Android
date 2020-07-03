package com.example.russiantpu.items;

import android.graphics.drawable.Drawable;

import com.example.russiantpu.enums.ContentType;

//класс представляет собой единицу превью статьи
public class Article extends Item {
    private String header; //заголовок
    private String fullText;
    private String date; //дата - пока что строка, с форматом даты не определились

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }


    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Article(int id, String header, String fullText, String date) {
        super(id, ContentType.ARTICLE);
        this.header = header;
        this.fullText = fullText;
        this.date = date;
    }

    public Article() {
        super(0, ContentType.ARTICLE);
    }
}
