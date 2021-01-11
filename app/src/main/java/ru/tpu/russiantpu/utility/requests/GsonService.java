package ru.tpu.russiantpu.utility.requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import ru.tpu.russiantpu.utility.requests.adapters.DateTypeAdapter;

//класс-сервис, отвечающий за
//сериализацию и десериализацию
//на базе библиотеки Gson
public class GsonService {

    private Gson gson;

    public GsonService() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        //gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        gson = gsonBuilder.create();
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

    public String fromObjectToJson(Object object) {
        return gson.toJson(object);
    }

    //метод получения одного объекта из строки json
    public String getFieldFromJson(String field, String json) {
        try {
            return new JSONObject(json).getString(field);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
