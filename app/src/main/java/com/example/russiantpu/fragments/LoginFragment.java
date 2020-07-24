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
import com.example.russiantpu.items.LinkItem;

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO
                *  post запрос на логин*/

                getActivity().startActivity(new Intent(getContext(), MainActivity.class));
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
