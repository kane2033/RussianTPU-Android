package com.example.russiantpu.items;

import com.example.russiantpu.enums.ContentType;

//класс, представляющий элемент бокового меню (1 уровень)
public class DrawerItem {
    private int id;
    private String name;
    private ContentType type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public DrawerItem(int id, String name, ContentType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
