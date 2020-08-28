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

    //конструктор без необходимости использования токенов
    public RequestService() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //временное увеличение таймаута с целью успешной регистрации
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
    }

    //конструктор для случаев, когда необхоим токен
    //передаем ссылку на sharedPreferences для получения токена
    public RequestService(SharedPreferencesService sharedPreferencesService) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //временное увеличение таймаута с целью успешной регистрации
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        //класс аунтефикатор автоматически обновит токен при истечении
        builder.authenticator(new TokenAuthenticator(sharedPreferencesService));
        client = builder.build();
    }

    //GET запрос на url с произвольным количеством параметров:
    //параметры вводятся форматом - название параметра, значение параметра, ...
    public void doRequest(String url, final GenericCallback<String> callback, String token, String language, String... params) {
        String builtUrl = buildUrlWithParams(url, params);
        Request request = new Request.Builder()
                .url(builtUrl)
                .addHeader("Authorization", "Bearer " + token) //JWT
                .addHeader("Accept-Language", language) //язык
                .build();

        enqueue(request, callback);
    }

    //GET запрос на url без токена
    public void doRequest(String url, String language, final GenericCallback<String> callback, String... params) {
        String builtUrl = buildUrlWithParams(url, params);
        Request request = new Request.Builder()
                .url(builtUrl)
                .addHeader("Accept-Language", language) //язык
                .build();

        enqueue(request, callback);
    }

    private String buildUrlWithParams(String url, String... params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + url).newBuilder();
        for (int i = 0; i < params.length; i++) {
            urlBuilder.addQueryParameter(params[i], params[++i]);
        }
        return urlBuilder.build().toString();
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

    //put запрос - в параметрах получаем строку формата json, которая отправляется в теле запроса
    public void doPutRequest(String url, final GenericCallback<String> callback, String token, String language, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_URL + url)
                .addHeader("Accept-Language", language) //язык
                .addHeader("Authorization", "Bearer " + token)
                .put(body)
                .build();

        enqueue(request, callback);
    }

    public String doPutRequestSync(String url, String token, String language) {
        RequestBody body = RequestBody.create("", null);
        Request request = new Request.Builder()
                .url(API_URL + url)
                .addHeader("Accept-Language", language) //язык
                .addHeader("Authorization", "Bearer " + token)
                .put(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String jsonBody = response.body().string();
            if (response.isSuccessful()) {
                Log.d("JSON_RESPONSE", "onResponse: " + jsonBody);
                return jsonBody; //тело ответа
            }
            else {
                Log.d("RESPONSE_ERR", "Response code: " + response.code() + "; text: " + jsonBody);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
                else { //(300; 500)
                    //возвращаем только сообщение из полученного json с временем, кодом, и сообщением ошибки
                    GsonService gsonService = new GsonService();
                    callback.onError(gsonService.getFieldFromJson("message", jsonBody));
                    Log.d("RESPONSE_ERR", "Response code: " + response.code() + "; text: " + jsonBody);
                }
            }
        });
    }

}
