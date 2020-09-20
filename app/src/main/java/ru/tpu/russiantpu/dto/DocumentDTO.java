package ru.tpu.russiantpu.dto;

/**
* Класс хранит документы
* */
public class DocumentDTO {
    private String name;
    private String loadDate;
    private String url;
    private String fileName;

    public DocumentDTO(String name, String loadDate, String url, String fileName) {
        this.name = name;
        this.loadDate = loadDate;
        this.url = url;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(String loadDate) {
        this.loadDate = loadDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
