package ru.tpu.russiantpu.dto;

public class CheckTokenDTO {
    private String token;
    private String email;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CheckTokenDTO(String token, String email) {
        this.token = token;
        this.email = email;
    }
}
