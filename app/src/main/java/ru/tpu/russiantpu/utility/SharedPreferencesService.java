package ru.tpu.russiantpu.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import ru.tpu.russiantpu.dto.UserDTO;

public class SharedPreferencesService {

    private final String preferencesFileKey = "credentials";
    private final String tokenKey = "token";
    private final String refreshTokenKey = "refreshToken";
    private final String emailTokenKey = "email";
    private final String firstNameKey = "firstName";
    private final String languageIdKey = "languageId";
    private final String languageNameKey = "languageName";
    private final String groupKey = "group";

    private final SharedPreferences sharedPreferences;

    public SharedPreferencesService(Activity activity) {
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

    public String getFirstName() {return sharedPreferences.getString(firstNameKey, "");}

    public String getLanguageId() {return sharedPreferences.getString(languageIdKey, "");}

    public String getLanguageName() {return sharedPreferences.getString(languageNameKey, "");}

    public String getGroupName() {return sharedPreferences.getString(groupKey, "");}

    public UserDTO getUser() {
        return new UserDTO(getEmail(), getFirstName(), getLanguageId(), getLanguageName());
    }

    public void setCredentials(String token, String refreshToken, UserDTO user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //сохраняем токены
        editor.putString(tokenKey, token);
        editor.putString(refreshTokenKey, refreshToken);
        //сохраняем пользователя
        editor.putString(emailTokenKey, user.getEmail());
        editor.putString(firstNameKey, user.getFirstName());
        editor.putString(languageIdKey, user.getLanguageId());
        editor.putString(languageNameKey, user.getLanguageName());
        editor.putString(groupKey, user.getGroupName());
        editor.commit();
    }

    //метод вызывается при выходе из аккаунта,
    //удаляет токены из sharedPreferences
    public void clearCredentials() {
        sharedPreferences.edit().clear().commit();
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

    public void setTokens(String token, String refreshToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tokenKey, token);
        editor.putString(refreshTokenKey, refreshToken);
        editor.commit();
    }

    public void setUser(UserDTO user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(emailTokenKey, user.getEmail());
        editor.putString(firstNameKey, user.getFirstName());
        editor.putString(languageIdKey, user.getLanguageId());
        editor.putString(languageNameKey, user.getLanguageName());
        editor.putString(groupKey, user.getGroupName());
        editor.commit();
    }

    public void setEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(emailTokenKey, email);
        editor.commit();
    }

    public void setFirstName(String firstName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(firstNameKey, firstName);
        editor.commit();
    }

    public void setLanguageId(String languageId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(languageIdKey, languageId);
        editor.commit();
    }

    public void setLanguageName(String languageName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(languageNameKey, languageName);
        editor.commit();
    }

    public void setGroupName(String groupName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(groupKey, groupName);
        editor.commit();
    }

}
