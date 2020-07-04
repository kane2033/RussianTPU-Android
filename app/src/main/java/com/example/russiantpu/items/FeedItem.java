package com.example.russiantpu.items;

import com.example.russiantpu.enums.ContentType;

//класс представляет собой единицу превью статьи
public class FeedItem extends Item{
    private String header; //заголовок
    private String previewText;
    private String date; //дата - пока что строка, с форматом даты не определились

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPreviewText() {
        return previewText;
    }

    public void setPreviewText(String previewText) {
        this.previewText = previewText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public FeedItem(String id, int position, String header, String previewText, String date) {
        super(id, position, ContentType.Article);
        this.header = header;
        this.previewText = previewText;
        this.date = date;
    }
}
