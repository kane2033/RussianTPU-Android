package com.example.russiantpu.utility;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

//класс-сервис, отвечающий за
//сериализацию и десериализацию
//на базе библиотеки Gson
public class GsonService {

    private Gson gson = new Gson();

    public GsonService() {

    }

    //получает на вход json строку и класс
    //возвращает десериализированный arraylist<clazz>
    public <T> ArrayList<T>  fromJsonToArrayList(String json, Class<T> clazz) {
        final Type listType = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return gson.fromJson(json, listType);
    }

    public <T> T fromJsonToObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
