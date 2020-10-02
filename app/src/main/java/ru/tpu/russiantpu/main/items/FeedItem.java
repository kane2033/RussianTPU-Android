package ru.tpu.russiantpu.main.items;

import android.os.Parcel;
import android.os.Parcelable;

import ru.tpu.russiantpu.main.enums.ContentType;

//класс представляет собой единицу превью статьи
public class FeedItem extends Item implements Parcelable {
    private String topic; //заголовок
    private String briefText;
    private String subject;
    private String createDate; //дата - пока что строка, с форматом даты не определились
    private String articleImage; //картинка хранится строкой base64

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBriefText() {
        return briefText;
    }

    public void setBriefText(String briefText) {
        this.briefText = briefText;
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

    public String getArticleImage() {
        return articleImage;
    }

    public void setArticleImage(String articleImage) {
        this.articleImage = articleImage;
    }

    public FeedItem(String id, int position, String topic, String briefText, String subject, String createDate, String articleImage) {
        super(id, position, ContentType.ARTICLE);
        this.topic = topic;
        this.briefText = briefText;
        this.subject = subject;
        this.createDate = createDate;
        this.articleImage = articleImage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.topic);
        dest.writeString(this.briefText);
        dest.writeString(this.subject);
        dest.writeString(this.createDate);
        dest.writeString(this.articleImage);
        dest.writeString(this.getId());
        dest.writeInt(this.getPosition());
        dest.writeString(getType().toString());
    }

    protected FeedItem(Parcel in) {
        this.topic = in.readString();
        this.briefText = in.readString();
        this.subject = in.readString();
        this.createDate = in.readString();
        this.articleImage = in.readString();
        setId(in.readString());
        setPosition(in.readInt());
        setType(ContentType.valueOf(in.readString()));
    }

    public static final Creator<FeedItem> CREATOR = new Creator<FeedItem>() {
        @Override
        public FeedItem createFromParcel(Parcel source) {
            return new FeedItem(source);
        }

        @Override
        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };
}
