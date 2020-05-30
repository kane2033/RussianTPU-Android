package com.example.russiantpu.utility;

//класс, представляющий элемент бокового меню (1 уровень)
public class DrawerItem {
    private int id;
    private String name;
    //private boolean isLink;

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

    public DrawerItem(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
