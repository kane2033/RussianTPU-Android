package com.example.russiantpu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.russiantpu.R;
import com.example.russiantpu.dto.UserDTO;
import com.example.russiantpu.utility.ErrorDialogService;
import com.example.russiantpu.utility.FormService;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;
import com.example.russiantpu.utility.SharedPreferencesService;
import com.example.russiantpu.utility.SpinnerValidatorAdapter;
import com.example.russiantpu.utility.ToastService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

public class ProfileActivity extends AppCompatActivity implements Validator.ValidationListener {

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.lastname_error) //optional, max 50
    private TextInputEditText lastNameInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    private TextInputEditText firstNameInput;

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.middlename_error) //optional, max 50
    private TextInputEditText middleNameInput;

    //ввод пола не валидируется
    private RadioGroup genderInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    private Spinner languageInput; //список выбора языка

    @Pattern(regex = "^(?=.{0,20}$).*", messageResId = R.string.phone_number) //optional, max 20
    private TextInputEditText phoneNumberInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    @Pattern(regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", messageResId = R.string.password_error)
    private TextInputEditText currentPasswordInput;
    //    private TextInputEditText currentPasswordInput;

    @Pattern(regex = "(^$)|(^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$)", messageResId = R.string.password_error) //пустая строка или regex пароля
    private TextInputEditText newPasswordInput;
    //    private TextInputEditText newPasswordInput;

    private Button saveButton;

    private LinearLayout formContainer;
    private LinearLayout formContainerEditElements;

    private SharedPreferencesService sharedPreferencesService;
    private RequestService requestService;
    private GsonService gsonService;
    private FormService formService;
    private ToastService toastService;

    private boolean areFieldsEditable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //все элементы формы в LinearLayout
        formContainer = findViewById(R.id.profile_form);
        formContainerEditElements = formContainer.findViewById(R.id.profile_form_edit_elements);

        lastNameInput = formContainer.findViewById(R.id.input_lastname);
        firstNameInput = formContainer.findViewById(R.id.input_firstname);
        middleNameInput = formContainer.findViewById(R.id.input_middlename);
        genderInput = formContainer.findViewById(R.id.input_gender);
        languageInput = formContainer.findViewById(R.id.input_language_spinner);
        phoneNumberInput = formContainer.findViewById(R.id.input_phone_number);
        newPasswordInput = formContainer.findViewById(R.id.input_new_password);
        currentPasswordInput = formContainer.findViewById(R.id.input_current_password);
        saveButton = formContainer.findViewById(R.id.button_save);
        ImageButton editButton = findViewById(R.id.button_edit);
        ImageButton logoutButton = findViewById(R.id.button_logout);

        //устанавливаем все поля нередактируемыми
        switchFieldsEditable();

        //валидируем содержимое фрагмента, поэтому передаем фрагмент в классы валидаторов
        final Validator validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(Spinner.class, new SpinnerValidatorAdapter()); //кастомный валидатор для списка языков

        //нажатие кнопки назад
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //при нажатии кнопки "сохранить изменения"
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        //редактирование информации юзера
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //включаем/выключаем поля для редактирования
                switchFieldsEditable();
                //отображаем кнопку "старый пароль" и "сохранить изменения"
            }
        });
        //выход из учетной записи юзера
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesService.clearCredentials(); //удаляем из памяти инфу о юзере
                startActivity(new Intent(ProfileActivity.this, AuthActivity.class)); //переходим на авторизацию
            }
        });

        sharedPreferencesService = new SharedPreferencesService(this);
        gsonService = new GsonService();
        formService = new FormService();
        toastService = new ToastService(getApplicationContext());

        final GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                //получаем всю информацию о юзере с сервиса
                final UserDTO user = gsonService.fromJsonToObject(jsonBody, UserDTO.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //заполняем поля
                        lastNameInput.setText(user.getLastName());
                        firstNameInput.setText(user.getFirstName());
                        middleNameInput.setText(user.getMiddleName());
                        formService.setSelectedGender(user.getGender(), genderInput); //устанавливаем пол radiobutton
                        formService.setSelectedLanguage(languageInput, getResources().getStringArray(R.array.languages_array_keys), user.getLanguage());
                        phoneNumberInput.setText(user.getPhoneNumber());
                    }
                });

            }

            @Override
            public void onError(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.profile_get_error), gsonService.getFieldFromJson("message", message), getSupportFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.profile_get_error), message, getSupportFragmentManager());
            }
        };

        final String token = sharedPreferencesService.getToken();
        final String language = sharedPreferencesService.getLanguage();
        final String email = sharedPreferencesService.getEmail();
        requestService = new RequestService();
        requestService.doRequest("user/profile/", callback, token, language, "email", email);
/*        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                requestService.doRequest("user/profile/", callback, token, language, "email", email);
            }
        });*/

    }

    //метод активации и деактивации полей формы
    private void switchFieldsEditable() {
        //отключаем/включаем все поля формы
        areFieldsEditable = !areFieldsEditable;
        for (int i = 0; i < formContainer.getChildCount(); i++) {
            View child = formContainer.getChildAt(i);
            child.setEnabled(areFieldsEditable);
        }
        RadioGroup radioGroup = formContainer.findViewById(R.id.input_gender);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(areFieldsEditable);
        }
        //отображаем/скрываем элементы редактирования информации юзера
        int visibility = areFieldsEditable ? View.VISIBLE : View.GONE;
        formContainerEditElements.setVisibility(visibility);
    }

    //при успешной валидации
    @Override
    public void onValidationSucceeded() {
        //отсылаем на сервис новую информацию о юзере
        final String token = sharedPreferencesService.getToken();
        String email = sharedPreferencesService.getEmail();
        String currentPassword = formService.getTextFromInput(currentPasswordInput);
        String newPassword = formService.getTextFromInput(newPasswordInput);
        String firstName = formService.getTextFromInput(firstNameInput);
        String lastName = formService.getTextFromInput(lastNameInput);
        String middleName = formService.getTextFromInput(middleNameInput);
        String gender = formService.getSelectedGender(genderInput);
        final String language = formService.getSelectedLanguage(languageInput, getResources().getStringArray(R.array.languages_array_keys));
        String phoneNumber = formService.getTextFromInput(phoneNumberInput);
        final UserDTO dto = new UserDTO(email, currentPassword, newPassword, firstName, lastName, middleName, gender, language, phoneNumber);

        final GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String message) {
                toastService.showToast(R.string.profile_save_success);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switchFieldsEditable(); //отключаем редактирование полей
                    }
                });
            }

            @Override
            public void onError(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.profile_save_error), gsonService.getFieldFromJson("message", message), getSupportFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.profile_save_error), message, getSupportFragmentManager());
            }
        };

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String json = gsonService.fromObjectToJson(dto);
                requestService.doPutRequest("user/edit", callback, token, language, json);
            }
        });
    }

    //если валидация не успешна
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getApplicationContext());

            //отображение ошибки
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(message);
            }
        }
    }
}