package com.example.russiantpu.items;

import com.example.russiantpu.enums.ContentType;

public abstract class Item {
    //по айди будет делаться get запрос на сервер
    //для получения содержимого раздела
    private int id;
    //по этой переменной решается,
    //какой фрагмент запустится при выборе элемента
    private ContentType type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public Item(int id, ContentType type) {
        this.id = id;
        this.type = type;
    }
}
