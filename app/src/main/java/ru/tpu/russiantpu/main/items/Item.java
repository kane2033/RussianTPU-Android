package ru.tpu.russiantpu.main.items;

import android.os.Parcel;
import android.os.Parcelable;

import ru.tpu.russiantpu.main.enums.ContentType;

public abstract class Item implements Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.position);
        dest.writeString(this.type.toString());
    }

    protected Item(Parcel in) {
        this.id = in.readString();
        this.position = in.readInt();
        this.type = ContentType.valueOf(in.readString());
    }

/*    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return null;
            //return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };*/
}
