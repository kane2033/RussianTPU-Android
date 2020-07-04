package com.example.russiantpu.utility;

import android.app.Activity;
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

    private OkHttpClient client = new OkHttpClient();
    private Activity activity = null;

    public RequestService() {

    }

    public RequestService(Activity activity) {
        this.activity = activity;
    }

    public void getDrawerItems(final GenericCallback<ArrayList<DrawerItem>> callback) {
        //List<DrawerItem> items = new ArrayList<>();
        Request request = new Request.Builder()
                .url("http://109.123.155.178:8080/menu/static")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonStr = response.body().string();
                    final Type listType = new TypeToken<ArrayList<DrawerItem>>(){}.getType();
                    ArrayList<DrawerItem> items = new Gson().fromJson(jsonStr, listType);

                    Log.d("GET_REQUEST","Количество предметов: " + items.size());

                    callback.onResponse(items);

/*                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            items = new Gson().fromJson(jsonStr, listType);
                            System.out.println("Количество предметов: " + items.size());
                            Log.d("GET_REQUEST","Количество предметов: " + items.size());
                        }
                    });*/
                }
            }
        });
    }
}
