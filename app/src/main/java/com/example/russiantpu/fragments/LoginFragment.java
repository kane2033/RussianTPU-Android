package com.example.russiantpu.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.russiantpu.MainActivity;
import com.example.russiantpu.R;
import com.example.russiantpu.dto.LoginDTO;
import com.example.russiantpu.dto.TokensDTO;
import com.example.russiantpu.items.LinkItem;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;

public class LoginFragment extends Fragment {

    private String fragmentTag = "AUTH_PREV_FRAGMENT";

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button gotoRegisterButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ScrollView layoutInflater = (ScrollView)inflater.inflate(R.layout.fragment_login, container, false);

        emailInput = layoutInflater.findViewById(R.id.input_email);
        passwordInput = layoutInflater.findViewById(R.id.input_password);
        loginButton = layoutInflater.findViewById(R.id.button_login);
        gotoRegisterButton = layoutInflater.findViewById(R.id.goto_register);

        final RequestService requestService = new RequestService();
        final GsonService gsonService = new GsonService();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginDTO dto = new LoginDTO(emailInput.getText().toString(), passwordInput.getText().toString(), true);
                final String json = gsonService.fromObjectToJson(dto);

                GenericCallback<String> callback = new GenericCallback<String>() {
                    @Override
                    public void onResponse(String jsonBody) {
                        //переносим в главную активити токены для последующего сохранения
                        TokensDTO tokens = gsonService.fromJsonToObject(jsonBody, TokensDTO.class);
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra("token", tokens.getToken());
                        intent.putExtra("refreshToken", tokens.getRefreshToken());
                        getActivity().startActivity(intent);
                    }
                };

                requestService.doPostRequest("auth/login", callback, json);
            }
        });

        //при нажатии на кнопку регистрации переходим во фрагмент регистрации
        gotoRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RegisterFragment()).addToBackStack(fragmentTag).commit();
            }
        });
        return layoutInflater;
    }

}
