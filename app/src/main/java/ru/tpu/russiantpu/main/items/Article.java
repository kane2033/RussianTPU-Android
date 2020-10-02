package ru.tpu.russiantpu.main.items;

import android.os.Parcel;
import android.os.Parcelable;

import ru.tpu.russiantpu.main.enums.ContentType;

//класс представляет собой единицу превью статьи
public class Article extends Item implements Parcelable {
    private String topic; //заголовок
    private String text;
    private String subject;
    private String createDate; //дата - пока что строка, с форматом даты не определились

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Article(String id, String topic, String text, String subject, String createDate) {
        super(id, 1, ContentType.ARTICLE);
        this.topic = topic;
        this.text = text;
        this.subject = subject;
        this.createDate = createDate;
    }

    public Article() {
        super("idString", 1, ContentType.ARTICLE);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.topic);
        dest.writeString(this.text);
        dest.writeString(this.subject);
        dest.writeString(this.createDate);
        dest.writeString(this.getId());
        dest.writeInt(this.getPosition());
        dest.writeString(getType().toString());
    }

    protected Article(Parcel in) {
        this.topic = in.readString();
        this.text = in.readString();
        this.subject = in.readString();
        this.createDate = in.readString();
        setId(in.readString());
        setPosition(in.readInt());
        setType(ContentType.valueOf(in.readString()));
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
