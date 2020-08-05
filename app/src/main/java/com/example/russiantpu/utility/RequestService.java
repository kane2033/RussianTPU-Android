package com.example.russiantpu.utility;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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
        client = new OkHttpClient();
    }

    //GET запрос на url с произвольным количеством параметров:
    //параметры вводятся форматом - название параметра, значение параметра, ...
    public void doRequest(String url, final GenericCallback<String> callback, String token, String... params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + url).newBuilder();
        for (int i = 0; i < params.length; i++) {
            urlBuilder.addQueryParameter(params[i], params[++i]);
        }
        String builtUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(builtUrl)
                .addHeader("Authorization", "Bearer " + token) //JWT
                .build();

        enqueue(request, callback);
    }

    //GET запрос на url без параметров
    public void doRequest(String url, final GenericCallback<String> callback, String token) {
        Request request = new Request.Builder()
                .url(API_URL + url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        enqueue(request, callback);
    }

    //post запрос - в параметрах получаем строку формата json, которая отправляется в теле запроса
    public void doPostRequest(String url, final GenericCallback<String> callback, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_URL + url)
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
                //callback.onFailure
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) { //code [200;300]
                    //при успешном запросе заносим код в переменную для случаев, когда требуется знать код
                    responseCode = response.code();
                    final String jsonBody = response.body().string(); //тело ответа
                    Log.d("JSON_RESPONSE", "onResponse: " + jsonBody);
                    callback.onResponse(jsonBody);
                }
                else {
                    //следует сделать отображение ошибок на экране:
                    //callback.onError()
                    Log.d("RESPONSE_ERR", "Response code: " + response.code() + "; text: " + response.body().string());
                }
            }
        });
    }

}
