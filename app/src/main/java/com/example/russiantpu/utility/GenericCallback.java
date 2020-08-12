package com.example.russiantpu.utility;

public interface GenericCallback<T> {
    void onResponse(T value);
    void onError(T value);
    void onFailure(T value);
}
