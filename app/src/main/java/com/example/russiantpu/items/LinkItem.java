package com.example.russiantpu.items;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.russiantpu.enums.ContentType;

import java.util.ArrayList;

//класс отображает один пункт в меню ссылок (кнопок)
public class LinkItem extends Item implements Parcelable {

    private String name;
    //ссылка на сторонний ресурс
    private String url;
    //айди статьи, по которой будет получена полная статья
    private String articleId;
    //дочерние пункты
    private ArrayList<LinkItem> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public ArrayList<LinkItem> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<LinkItem> children) {
        this.children = children;
    }

    public LinkItem(String name, String id, int position, ContentType type, ArrayList<LinkItem> children) {
        super(id, position, type);
        this.name = name;
        this.children = children;
    }

    public LinkItem(String name, String id, int position, ContentType type, String url, String articleId) {
        super(id, position, type);
        this.name = name;
        this.url = url;
        this.articleId = articleId;
    }
    //стоит добавить отдельный конструктор, когда нет link или articleId?

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.articleId);
        dest.writeList(this.children);
        dest.writeString(getId());
        dest.writeInt(getPosition());
        dest.writeString(getType().toString());
    }

    protected LinkItem(Parcel in) {
        this.name = in.readString();
        this.url = in.readString();
        this.articleId = in.readString();
        this.children = new ArrayList<>();
        in.readList(this.children, LinkItem.class.getClassLoader());
        setId(in.readString());
        setPosition(in.readInt());
        setType(ContentType.valueOf(in.readString()));
    }

    public static final Parcelable.Creator<LinkItem> CREATOR = new Parcelable.Creator<LinkItem>() {
        @Override
        public LinkItem createFromParcel(Parcel source) {
            return new LinkItem(source);
        }

        @Override
        public LinkItem[] newArray(int size) {
            return new LinkItem[size];
        }
    };
}
