package ru.tpu.russiantpu.items;

import ru.tpu.russiantpu.enums.ContentType;

//класс представляет собой единицу превью статьи
public class Article extends Item {
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
}
