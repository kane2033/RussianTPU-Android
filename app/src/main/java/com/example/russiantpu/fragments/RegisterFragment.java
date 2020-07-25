package com.example.russiantpu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.russiantpu.R;
import com.example.russiantpu.dto.RegisterDTO;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;

public class RegisterFragment extends Fragment {

    private final String fragmentTag = String.valueOf(R.string.prev_auth_frag_tag);

    private EditText emailInput;
    private EditText passwordInput;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private RadioGroup genderInput;
    private EditText languageInput;
    private EditText phoneNumberInput;
    private Button registerButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ScrollView layoutInflater = (ScrollView)inflater.inflate(R.layout.fragment_register, container, false);

        emailInput = layoutInflater.findViewById(R.id.input_email);
        passwordInput = layoutInflater.findViewById(R.id.input_password);
        firstNameInput = layoutInflater.findViewById(R.id.input_firstname);
        lastNameInput = layoutInflater.findViewById(R.id.input_lastname);
        genderInput = layoutInflater.findViewById(R.id.input_gender);
        languageInput = layoutInflater.findViewById(R.id.input_language);
        phoneNumberInput = layoutInflater.findViewById(R.id.input_phone_number);
        registerButton = layoutInflater.findViewById(R.id.button_register);

        final RequestService requestService = new RequestService();
        final GsonService gsonService = new GsonService();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //берем выбраный пол
                int selectedId = genderInput.getCheckedRadioButtonId();
                RadioButton radioButton = layoutInflater.findViewById(selectedId);
                String gender = radioButton.getText().toString();

                //заполняем дто регистрации, которое будем отправлять на сервис
                RegisterDTO dto = new RegisterDTO(emailInput.getText().toString(), passwordInput.getText().toString(), firstNameInput.getText().toString(),
                        lastNameInput.getText().toString(), gender, languageInput.getText().toString(), phoneNumberInput.getText().toString());

                String json = gsonService.fromObjectToJson(dto);

                GenericCallback<String> callback = new GenericCallback<String>() {
                    @Override
                    public void onResponse(String jsonBody) {

                        //нужно уведомить об успешной регистрации

                        // переход обратно на фрагмент логина при успешной регистрации
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new LoginFragment()).addToBackStack(fragmentTag).commit();
                    }
                };
                requestService.doPostRequest("auth/register", callback, json);

            }
        });

        return layoutInflater;
    }
}
