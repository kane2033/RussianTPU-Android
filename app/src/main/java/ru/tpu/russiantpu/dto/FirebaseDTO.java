package ru.tpu.russiantpu.dto;

/**
* DTO для регистрации пользователя на уведомления Firebase
* */
public class FirebaseDTO {
    private String email;
    private String token; //firebase token

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public FirebaseDTO(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
