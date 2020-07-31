package com.example.russiantpu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.russiantpu.MainActivity;
import com.example.russiantpu.R;
import com.example.russiantpu.dto.LoginByProviderDTO;
import com.example.russiantpu.dto.LoginDTO;
import com.example.russiantpu.dto.UserDTO;
import com.example.russiantpu.dto.TokensDTO;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GoogleAuthService;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;
import com.example.russiantpu.utility.SharedPreferencesService;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private final String fragmentTag = String.valueOf(R.string.prev_auth_frag_tag);

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button gotoRegisterButton;
    private ImageView loginGoogle;
    private ImageView loginFacebook;

    private RequestService requestService;
    private GsonService gsonService;
    private GoogleAuthService googleAuth;

    private GenericCallback<String> toMainActivityCallback;
    private GenericCallback<String> providerAuthCallback;
    private GenericCallback<String> postIdTokenCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ScrollView layoutInflater = (ScrollView)inflater.inflate(R.layout.fragment_login, container, false);

        emailInput = layoutInflater.findViewById(R.id.input_email);
        passwordInput = layoutInflater.findViewById(R.id.input_password);
        loginButton = layoutInflater.findViewById(R.id.button_login);
        gotoRegisterButton = layoutInflater.findViewById(R.id.goto_register);
        loginGoogle = layoutInflater.findViewById(R.id.button_login_google);
        loginFacebook = layoutInflater.findViewById(R.id.button_login_facebook);

        loginButton.setOnClickListener(this);
        gotoRegisterButton.setOnClickListener(this);
        loginGoogle.setOnClickListener(this);
        loginFacebook.setOnClickListener(this);

        requestService = new RequestService();
        gsonService = new GsonService();
        //передаем ссылку на фрагмент для вызова onActivityResult через класс
        googleAuth = new GoogleAuthService(this);

        //коллбэк при успешном запросе - переход в основное активити
        toMainActivityCallback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                //получение токенов с сервиса
                TokensDTO tokens = gsonService.fromJsonToObject(jsonBody, TokensDTO.class);

                //сохраняем JWT токен в sharedPreferences для последующего использования
                SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(getActivity());
                sharedPreferencesService.setCredentials(tokens.getToken(), tokens.getRefreshToken(), tokens.getUser().getEmail());

                getActivity().startActivity(new Intent(getContext(), MainActivity.class));
            }
        };

        //коллбэк, когда при авторизации через сторонние сервисы
        //оказывается, что пользователь не зарегистрирован в бд
        providerAuthCallback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                if (requestService.getResponseCode() == 210) { //если пользователь отсутствует в бд
                    //получаем данные пользователя, вошедшего через сторонний сервис
                    UserDTO dto = gsonService.fromJsonToObject(jsonBody, UserDTO.class);

                    //переносим полученные данные пользователя в фрагмент регистрации
                    Bundle args = new Bundle();
                    args.putParcelable("registerDTO", dto);
                    RegisterFragment fragment = new RegisterFragment();
                    fragment.setArguments(args);

                    //переходим к фрагменту регистрации
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            fragment).addToBackStack(fragmentTag).commit();
                }
                else {
                    toMainActivityCallback.onResponse(jsonBody); //обычный коллбэк, при котором переходим в приложение
                }
            }
        };

        return layoutInflater;
    }

    @Override
    public void onClick(View v) {
        String json;
        switch (v.getId()) {
            case R.id.button_login:
                json = gsonService.fromObjectToJson(new LoginDTO(emailInput.getText().toString(), passwordInput.getText().toString(), true));
                requestService.doPostRequest("auth/login", toMainActivityCallback, json);
                break;
            case R.id.goto_register:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RegisterFragment()).addToBackStack(fragmentTag).commit();
                break;
            case R.id.button_login_google:
                //инициализируем логин через google,
                //авторизация продолжается в onActivityResult
                googleAuth.initLogin();
                break;
            case R.id.button_login_facebook:
                //login via facebook
                break;
            default:
                break;
        }
    }

    //после авторизации через активити стороннего сервиса
    //вызывается этот метод, в котором берется idToken и
    //отсылкается на сервис
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == googleAuth.getRC_SIGN_IN()) {
            String idToken = googleAuth.handleSignInResult(data);
            if (idToken != null) {
                //делаем запрос
                String json = gsonService.fromObjectToJson(new LoginByProviderDTO(getResources().getString(R.string.provider_google), idToken));
                requestService.doPostRequest("auth/login/provider", providerAuthCallback, json);
            }
        }
    }
}
