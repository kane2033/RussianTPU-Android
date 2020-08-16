package com.example.russiantpu.utility;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestService {

    private final String API_URL = "http://109.123.155.178:8080/api/";
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;

    private int responseCode;

    public int getResponseCode() {
        return responseCode;
    }

    public RequestService() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //временное увеличение таймаута с целью успешной регистрации
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
    }

    //GET запрос на url с произвольным количеством параметров:
    //параметры вводятся форматом - название параметра, значение параметра, ...
    public void doRequest(String url, final GenericCallback<String> callback, String token, String language, String... params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + url).newBuilder();
        for (int i = 0; i < params.length; i++) {
            urlBuilder.addQueryParameter(params[i], params[++i]);
        }
        String builtUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(builtUrl)
                .addHeader("Authorization", "Bearer " + token) //JWT
                .addHeader("Accept-Language", language) //язык
                .build();

        enqueue(request, callback);
    }

    //GET запрос на url без параметров
    public void doRequest(String url, final GenericCallback<String> callback, String token, String language) {
        Request request = new Request.Builder()
                .url(API_URL + url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Accept-Language", language) //язык
                .build();

        enqueue(request, callback);
    }

    //post запрос - в параметрах получаем строку формата json, которая отправляется в теле запроса
    public void doPostRequest(String url, final GenericCallback<String> callback, String language, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_URL + url)
                .addHeader("Accept-Language", language) //язык
                .post(body)
                .build();

        enqueue(request, callback);
    }

    //делает запрос на url
    //возвращает тело ответа в json, доступное в реализации callback
    private void enqueue(final Request request, final GenericCallback<String> callback) {
        client.newCall(request).enqueue(new Callback() { //enqueue - асинхр., execute - синхр.
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                responseCode = response.code(); //заносим код в переменную для случаев, когда требуется знать код
                final String jsonBody = response.body().string(); //тело ответа
                if (response.isSuccessful()) { //code [200;300]
                    Log.d("JSON_RESPONSE", "onResponse: " + jsonBody);
                    callback.onResponse(jsonBody);
                }
                else {
                    callback.onError(jsonBody);
                    Log.d("RESPONSE_ERR", "Response code: " + response.code() + "; text: " + jsonBody);
                }
            }
        });
    }

}
