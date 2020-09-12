package ru.tpu.russiantpu.main.items;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import ru.tpu.russiantpu.main.enums.ContentType;

//класс отображает один пункт в меню ссылок (кнопок)
public class LinkItem extends Item implements Parcelable {

    private String name;
    //ссылка на сторонний ресурс
    private String url;
    //айди статьи, по которой будет получена полная статья
    private String idArticle;
    //дочерние пункты
    private ArrayList<LinkItem> children;
    //картинка
    private String image;

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

    public String getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(String idArticle) {
        this.idArticle = idArticle;
    }

    public ArrayList<LinkItem> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<LinkItem> children) {
        this.children = children;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.idArticle);
        dest.writeList(this.children);
        dest.writeString(this.image);
        dest.writeString(getId());
        dest.writeInt(getPosition());
        dest.writeString(getType().toString());
    }

    protected LinkItem(Parcel in) {
        this.name = in.readString();
        this.url = in.readString();
        this.idArticle = in.readString();
        this.children = new ArrayList<>();
        in.readList(this.children, LinkItem.class.getClassLoader());
        this.image = in.readString();
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
