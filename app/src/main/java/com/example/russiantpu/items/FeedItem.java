package com.example.russiantpu.items;

import android.graphics.drawable.Drawable;

import com.example.russiantpu.enums.ContentType;

//класс представляет собой единицу превью статьи
public class FeedItem {

    //по айди будет делаться get запрос на сервер
    // для получения содержимого раздела
    private int id;
    //по этой переменной решается,
    //какой фрагмент запустится при выборе элемента
    //для этого класса лучше бы убрать это поле
    private final ContentType type = ContentType.ARTICLE;
    private String header; //заголовок
    private Drawable image; //картинка (выбор нужного класса под вопросом)
    private String previewText;
    private String date; //дата - пока что строка, с форматом даты не определились

    public ContentType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
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

    public FeedItem(int id, String header, Drawable image, String previewText, String date) {
        this.id = id;
        this.header = header;
        this.image = image;
        this.previewText = previewText;
        this.date = date;
    }
}
