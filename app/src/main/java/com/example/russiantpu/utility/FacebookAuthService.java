package com.example.russiantpu.utility;

import androidx.fragment.app.Fragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class FacebookAuthService {
    private Fragment fragment;
    private CallbackManager callbackManager;

    public CallbackManager getCallbackManager() {return  callbackManager;}

    public FacebookAuthService(Fragment fragment, final GenericCallback<String> callback) {
        this.fragment = fragment;

        //инициализация коллбэка при логине
        initCallback(callback);
    }

    //метод инициализирует коллбэк при логине
    //параметр метода - коллбэк, в котором будет использован токен
    private void initCallback(final GenericCallback<String> callback) {
        callbackManager = CallbackManager.Factory.create();
        //loginButton.setPermissions("email");
        //loginButton.setFragment(fragment);

/*        //установка действия при нажатии кнопки
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(fragment, Arrays.asList("public_profile", "email"));
            }
        });*/

        //регистрация коллбэка
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //возвращаем токен через коллбэк
                callback.onResponse(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                //отмена
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                //Log.d("FB_ERR", "FB_AUTH EXCEPTION: " + exception.toString());
            }
        });
    }

    //метод запускает активити с логином
    public void initLogin() {
        LoginManager.getInstance().logInWithReadPermissions(fragment, Arrays.asList("public_profile", "email"));
    }
}
