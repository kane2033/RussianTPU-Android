package com.example.russiantpu.utility;

//коллбэк для VK
public interface VKTokenCallback {
    void onResponse(String token, Integer userId, String email);
}