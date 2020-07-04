package com.example.russiantpu.items;

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

    public Article(String id, String header, String fullText, String date) {
        super(id, 1, ContentType.Article);
        this.header = header;
        this.fullText = fullText;
        this.date = date;
    }

    public Article() {
        super("idString", 1, ContentType.Article);
    }
}
