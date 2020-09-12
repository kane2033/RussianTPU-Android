package ru.tpu.russiantpu.utility.callbacks;

//коллбэк для VK
public interface VKTokenCallback {
    void onResponse(String token, Integer userId, String email);
}
