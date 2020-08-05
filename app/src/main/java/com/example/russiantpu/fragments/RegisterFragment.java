package com.example.russiantpu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.russiantpu.R;
import com.example.russiantpu.dto.UserDTO;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;

public class RegisterFragment extends Fragment {

    private final String fragmentTag = String.valueOf(R.string.prev_auth_frag_tag);

    private EditText emailInput;
    private EditText passwordInput;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText middleNameInput;
    private RadioGroup genderInput;
    private EditText languageInput;
    private EditText phoneNumberInput;
    private Button registerButton;

    private UserDTO dto = new UserDTO();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ScrollView layoutInflater = (ScrollView)inflater.inflate(R.layout.fragment_register, container, false);

        emailInput = layoutInflater.findViewById(R.id.input_email);
        passwordInput = layoutInflater.findViewById(R.id.input_password);
        firstNameInput = layoutInflater.findViewById(R.id.input_firstname);
        lastNameInput = layoutInflater.findViewById(R.id.input_lastname);
        middleNameInput = layoutInflater.findViewById(R.id.input_middlename);
        genderInput = layoutInflater.findViewById(R.id.input_gender);
        languageInput = layoutInflater.findViewById(R.id.input_language);
        phoneNumberInput = layoutInflater.findViewById(R.id.input_phone_number);
        registerButton = layoutInflater.findViewById(R.id.button_register);

        final RequestService requestService = new RequestService();
        final GsonService gsonService = new GsonService();

        //если юзер регистрируется после авторизации
        //через сторонний сервис, заполняем имеющиеся поля
        if (getArguments() != null) {
            dto = getArguments().getParcelable("registerDTO");
            emailInput.setText(dto.getEmail());
            firstNameInput.setText(dto.getFirstName());
            lastNameInput.setText(dto.getLastName());

            //запрещаем редактировать уже заполненные поля
            emailInput.setEnabled(false);
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //берем выбраный пол
                String gender;
                int selectedId = genderInput.getCheckedRadioButtonId();
                if (selectedId == -1) { //если пол не выбран
                    gender = null;
                }
                else {
                    RadioButton radioButton = layoutInflater.findViewById(selectedId);
                    gender = radioButton.getText().toString();
                }


                //заполняем дто регистрации, которое будем отправлять на сервис
                //если поля не заполнены, они равны null
                dto.updateFields(getTextFromInput(emailInput), getTextFromInput(passwordInput), getTextFromInput(firstNameInput),
                        getTextFromInput(lastNameInput), getTextFromInput(middleNameInput), gender, getTextFromInput(languageInput),
                        getTextFromInput(phoneNumberInput));

                //если регистрация происходит через сторонние сервисы (поле != null), выбираем соответствующий юрл
                String url = dto.getProvider() != null ? "auth/register/provider" : "auth/register";

                String json = gsonService.fromObjectToJson(dto);

                GenericCallback<String> callback = new GenericCallback<String>() {
                    @Override
                    public void onResponse(String jsonBody) {

                        //нужно уведомить об успешной регистрации
                        //нужно уведомить об процессе отправки запроса

                        // переход обратно на фрагмент логина при успешной регистрации
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new LoginFragment()).addToBackStack(fragmentTag).commit();
                    }
                };

                requestService.doPostRequest(url, callback, json);

            }
        });

        return layoutInflater;
    }

    //метод возвращает текст из EditText, если таковой имеется,
    //иначе возвращается null
    private String getTextFromInput(EditText editText) {
        if (editText.getText() != null){
            return editText.getText().toString();
        }
        else {
            return null;
        }
    }
}
