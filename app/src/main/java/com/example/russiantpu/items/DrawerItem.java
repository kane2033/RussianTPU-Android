package com.example.russiantpu.items;

import com.example.russiantpu.enums.ContentType;

//класс, представляющий элемент бокового меню (1 уровень)
public class DrawerItem extends Item {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DrawerItem(int id, String name, ContentType type) {
        super(id, type);
        this.name = name;
    }
}
