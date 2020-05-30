package com.example.russiantpu.utility;

//класс отображает один пункт в меню ссылок (кнопок)
public class LinkItem {

    private String name;
    //по айди будет делаться get запрос на сервер
    // для получения содержимого раздела
    private int id;
    //по этой переменной решается,
    // какой фрагмент запустится при выборе элемента
    private boolean isLink;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLink() {
        return isLink;
    }

    public void setLink(boolean link) {
        isLink = link;
    }

    public LinkItem(String name, int id, boolean isLink) {
        this.name = name;
        this.id = id;
        this.isLink = isLink;
    }
}
