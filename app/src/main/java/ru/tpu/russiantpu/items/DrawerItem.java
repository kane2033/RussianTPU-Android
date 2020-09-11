package ru.tpu.russiantpu.items;

import ru.tpu.russiantpu.enums.ContentType;

//класс, представляющий элемент бокового меню (1 уровень)
public class DrawerItem extends Item {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DrawerItem(String id, int position, String name, ContentType type) {
        super(id, position, type);
        this.name = name;
    }
}
