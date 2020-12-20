package ru.tpu.russiantpu.dto;

public class LanguageDTO {
    private final String id;
    private final String name;
    private final String shortName;
    private final String image;

    public LanguageDTO(String id, String name, String shortName, String image) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getImage() {
        return image;
    }
}
