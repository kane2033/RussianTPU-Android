package com.example.russiantpu.items;

import com.example.russiantpu.enums.ContentType;

//класс представляет собой единицу превью статьи
public class FeedItem extends Item{
    private String topic; //заголовок
    private String briefText;
    private String subject;
    private String createDate; //дата - пока что строка, с форматом даты не определились

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

    public FeedItem(String id, int position, String topic, String briefText, String subject, String createDate) {
        super(id, position, ContentType.ARTICLE);
        this.topic = topic;
        this.briefText = briefText;
        this.subject = subject;
        this.createDate = createDate;
    }
}
