package ru.tpu.russiantpu.main.items;

import ru.tpu.russiantpu.main.enums.ContentType;

public abstract class Item {
    //по айди будет делаться get запрос на сервер
    //для получения содержимого раздела
    private String id;
    //позиция пункта в меню
    private int position;
    //по этой переменной решается,
    //какой фрагмент запустится при выборе элемента
    private ContentType type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Item(String id, int position, ContentType type) {
        this.id = id;
        this.position = position;
        this.type = type;
    }

    public Item() {}
}
