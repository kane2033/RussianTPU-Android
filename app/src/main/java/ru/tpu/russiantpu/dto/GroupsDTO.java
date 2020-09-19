package ru.tpu.russiantpu.dto;

//DTO групп ТПУ
public class GroupsDTO {
    private String idGroup;
    private String groupName;
    private String internalGroupId;

    public GroupsDTO(String idGroup, String groupName, String internalGroupId) {
        this.idGroup = idGroup;
        this.groupName = groupName;
        this.internalGroupId = internalGroupId;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getInternalGroupId() {
        return internalGroupId;
    }

    public void setInternalGroupId(String internalGroupId) {
        this.internalGroupId = internalGroupId;
    }
}
