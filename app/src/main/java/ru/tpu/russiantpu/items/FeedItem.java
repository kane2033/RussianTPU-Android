package ru.tpu.russiantpu.items;

import ru.tpu.russiantpu.enums.ContentType;

//класс представляет собой единицу превью статьи
public class FeedItem extends Item{
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
}
