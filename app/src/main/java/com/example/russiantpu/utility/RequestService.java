package com.example.russiantpu.utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestService {

    private final String API_URL = "http://109.123.155.178:8080/";
    private OkHttpClient client = new OkHttpClient();
    private Request request = null; //стоит сделать поля локальными?

    public RequestService() {

    }

    //запрос на url с параметрами
    public void doRequest(String url, String paramName, String paramValue, final GenericCallback<String> callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + url).newBuilder();
        urlBuilder.addQueryParameter(paramName, paramValue);
        String builtUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(builtUrl)
                .build();

        enqueue(request, callback);
    }

    //запрос на url без параметров
    public void doRequest(String url, final GenericCallback<String> callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        enqueue(request, callback);
    }

    //делает запрос на url
    //возвращает тело ответа в json, доступное в реализации callback
    private void enqueue(Request request, final GenericCallback<String> callback) {
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

}
