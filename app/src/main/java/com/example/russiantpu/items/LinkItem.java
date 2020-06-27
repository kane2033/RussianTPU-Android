package com.example.russiantpu.items;

import com.example.russiantpu.enums.ContentType;

//класс отображает один пункт в меню ссылок (кнопок)
public class LinkItem {

    private String name;
    //по айди будет делаться get запрос на сервер
    // для получения содержимого раздела
    private int id;
    //по этой переменной решается,
    // какой фрагмент запустится при выборе элемента
    private ContentType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LinkItem(String name, int id, ContentType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }
}
