package com.example.russiantpu.items;

import com.example.russiantpu.enums.ContentType;

//класс отображает один пункт в меню ссылок (кнопок)
public class LinkItem extends Item {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkItem(String name, String id, int position, ContentType type) {
        super(id, position, type);
        this.name = name;
    }
}
