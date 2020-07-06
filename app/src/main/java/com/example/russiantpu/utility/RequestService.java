package com.example.russiantpu.utility;

import android.util.Log;

import com.example.russiantpu.items.DrawerItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestService {

    private final String API_URL = "http://109.123.155.178:8080/";
    private OkHttpClient client = new OkHttpClient();
    private Request request = null; //стоит сделать поля локальными?

    public RequestService() {

    }

    //делает запрос на url
    //возвращает тело ответа в json, доступное в реализации callback
    public void doRequest(String url, final GenericCallback<String> callback) {
        Request request = new Request.Builder()
                .url(API_URL + url)
                .build();
        client.newCall(request).enqueue(new Callback() { //enqueue - асинхр., execute - синхр.
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonBody = response.body().string(); //тело ответа
                    callback.onResponse(jsonBody);
                }
            }
        });
    }

    //делает запрос на получение статического меню
    //возвращает десериализированный список пунктов меню
    public void getDrawerItems(final GenericCallback<ArrayList<DrawerItem>> callback) {
        Request request = new Request.Builder()
                .url(API_URL + "menu/static")
                .build();
        client.newCall(request).enqueue(new Callback() { //enqueue - асинхр., execute - синхр.
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonStr = response.body().string(); //тело ответа
                    final Type listType = new TypeToken<ArrayList<DrawerItem>>(){}.getType();

                    ArrayList<DrawerItem> items = new Gson().fromJson(jsonStr, listType); //десериализация
                    Log.d("GET_REQUEST","Количество предметов: " + items.size());

                    //возврат полученного массива предметов через интерфейс коллбек
                    //(в реализации обрабатывается полученное значение
                    callback.onResponse(items);
                }
            }
        });
    }
}
