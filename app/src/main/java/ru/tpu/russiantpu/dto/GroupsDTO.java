package ru.tpu.russiantpu.dto;

//DTO групп ТПУ
public class GroupsDTO {
    private String id;
    private String name;
    private String internalGroupId;

    public GroupsDTO(String id, String name, String internalGroupId) {
        this.id = id;
        this.name = name;
        this.internalGroupId = internalGroupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternalGroupId() {
        return internalGroupId;
    }

    public void setInternalGroupId(String internalGroupId) {
        this.internalGroupId = internalGroupId;
    }
}
