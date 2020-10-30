package ru.tpu.russiantpu.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDTO implements Parcelable {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private String languageId;
    private String languageName;
    private String phoneNumber;
    private String groupName;
    private String provider;
    //поле для активити профиля
    private String newPassword;

    public UserDTO() {}

    //конструктор для обновления информации о пользователе в активити профиля
    public UserDTO(String email, String password, String newPassword, String firstName,
                   String lastName, String middleName, String groupName, String gender,
                   String languageId, String languageName, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.newPassword = newPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.groupName = groupName;
        this.gender = gender;
        this.languageId = languageId;
        this.languageName = languageName;
        this.phoneNumber = phoneNumber;
    }

    public UserDTO(String email, String firstName, String languageId, String languageName) {
        this.email = email;
        this.firstName = firstName;
        this.languageId = languageId;
        this.languageName = languageName;
    }

    //метод используется в тех случаях, когда нельзя создавать новый экземпляр объекта,
    //потому что в нем может храниться информация (provider)
    public void updateFields(String email, String password, String firstName,
                             String lastName, String middleName, String groupName, String gender,
                             String language, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.groupName = groupName;
        this.gender = gender;
        this.languageId = language;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /*
    * Реализация Parcelable
    * */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.provider);
    }

    protected UserDTO(Parcel in) {
        this.email = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.provider = in.readString();
    }

    public static final Creator<UserDTO> CREATOR = new Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel source) {
            return new UserDTO(source);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };
}
