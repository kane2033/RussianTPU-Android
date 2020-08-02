package com.example.russiantpu.utility;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.russiantpu.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class FacebookAuthService {

    private LoginButton loginButton;
    private Fragment fragment;
    private CallbackManager callbackManager;

    public CallbackManager getCallbackManager() {return  callbackManager;}

    public FacebookAuthService(LoginButton loginButton, Fragment fragment) {
        this.loginButton = loginButton;
        this.fragment = fragment;
    }

    public void initCallback(final GenericCallback<String> callback) {
        callbackManager = CallbackManager.Factory.create();
        loginButton.setPermissions("email");
        loginButton.setFragment(fragment);

        //регистрация коллбэка
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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
}
