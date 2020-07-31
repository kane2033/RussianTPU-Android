package com.example.russiantpu.dto;

//Data Transfer Object для логина через сторонние сервисы
// (google, facebook, vk)
public class LoginByProviderDTO {
    private String provider;
    private String token;
    //поля нужны для vk
    private String userId;
    private String email;

    public LoginByProviderDTO(String provider, String token) {
        this.provider = provider;
        this.token = token;
    }

    //если provider == vk, заполняем дополнительные поля
    public LoginByProviderDTO(String provider, String token, String userId, String email) {
        this.provider = provider;
        this.token = token;
        this.userId = userId;
        this.email = email;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
