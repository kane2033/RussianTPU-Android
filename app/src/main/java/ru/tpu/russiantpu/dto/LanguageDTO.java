package ru.tpu.russiantpu.dto;

public class LanguageDTO {
    private final String id;
    private final String fullName;
    private final String shortName;
    private final String image;

    public LanguageDTO(String id, String fullName, String shortName, String image) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getImage() {
        return image;
    }
}
