package com.example.russiantpu.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesService {

    private final String preferencesFileKey = "credentials";
    private final String tokenKey = "token";
    private final String refreshTokenKey = "refreshToken";
    private final String emailTokenKey = "email";

    private final Activity activity;
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesService(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences(preferencesFileKey, Context.MODE_PRIVATE);
    }

    //получение JWT токена
    public String getToken() {
        return sharedPreferences.getString(tokenKey, "");
    }

    //получение токена, обновляющего сеанс
    public String getRefreshToken() {
        return sharedPreferences.getString(refreshTokenKey, "");
    }

    public String getEmail() {
        return sharedPreferences.getString(emailTokenKey, "");
    }

    public void setCredentials(String token, String refreshToken, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tokenKey, token);
        editor.putString(refreshTokenKey, refreshToken);
        editor.putString(emailTokenKey, email);
        editor.commit();
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tokenKey, token);
        editor.commit();
    }

    public void setRefreshToken(String refreshToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(refreshTokenKey, refreshToken);
        editor.commit();
    }

    public void setEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(emailTokenKey, email);
        editor.commit();
    }

}
