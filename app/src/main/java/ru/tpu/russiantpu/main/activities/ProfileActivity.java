package ru.tpu.russiantpu.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.auth.activities.AuthActivity;
import ru.tpu.russiantpu.dto.UserDTO;
import ru.tpu.russiantpu.utility.FormService;
import ru.tpu.russiantpu.utility.LocaleService;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.SpinnerValidatorAdapter;
import ru.tpu.russiantpu.utility.StartActivityService;
import ru.tpu.russiantpu.utility.ToastService;
import ru.tpu.russiantpu.utility.callbacks.DialogCallback;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.dialogFragmentServices.DialogService;
import ru.tpu.russiantpu.utility.dialogFragmentServices.ErrorDialogService;
import ru.tpu.russiantpu.utility.notifications.FirebaseNotificationService;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;

public class ProfileActivity extends AppCompatActivity implements Validator.ValidationListener {

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.lastname_error) //optional, max 50
    private TextInputEditText lastNameInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    private TextInputEditText firstNameInput;

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.middlename_error) //optional, max 50
    private TextInputEditText middleNameInput;

    //ввод пола не валидируется
    private Spinner genderInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    private Spinner languageInput; //список выбора языка

    @Pattern(regex = "^(?=.{0,20}$).*", messageResId = R.string.phone_number) //optional, max 20
    private TextInputEditText phoneNumberInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    @Pattern(regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!&^%$#@_|\\/\\\\]{8,}$", messageResId = R.string.password_error)
    private TextInputEditText currentPasswordInput;

    @Pattern(regex = "(^$)|(^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!&^%$#@_|\\/\\\\]{8,}$)", messageResId = R.string.password_error) //пустая строка или regex пароля
    private TextInputEditText newPasswordInput;

    private Button saveButton;
    private ContentLoadingProgressBar progressBar;

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

        //вспомогательные классы (сервисы)
        sharedPreferencesService = new SharedPreferencesService(this);
        gsonService = new GsonService();
        formService = new FormService();
        toastService = new ToastService(this);

        //установка языка интерфейса приложения
        LocaleService.setLocale(this, sharedPreferencesService.getLanguage());

        setContentView(R.layout.activity_profile);

        //все элементы формы в LinearLayout
        formContainer = findViewById(R.id.profile_form);
        formContainerEditElements = formContainer.findViewById(R.id.profile_form_edit_elements);

        lastNameInput = formContainer.findViewById(R.id.input_lastname);
        firstNameInput = formContainer.findViewById(R.id.input_firstname);
        middleNameInput = formContainer.findViewById(R.id.input_middlename);
        genderInput = formContainer.findViewById(R.id.input_gender_spinner);
        languageInput = formContainer.findViewById(R.id.input_language_spinner);
        phoneNumberInput = formContainer.findViewById(R.id.input_phone_number);
        newPasswordInput = formContainer.findViewById(R.id.input_new_password);
        currentPasswordInput = formContainer.findViewById(R.id.input_current_password);
        saveButton = formContainer.findViewById(R.id.button_save);
        ImageButton editButton = findViewById(R.id.button_edit);
        ImageButton logoutButton = findViewById(R.id.button_logout);
        progressBar = findViewById(R.id.progress_bar);

        //устанавливаем все поля нередактируемыми
        switchFieldsEditable();

        //валидируем содержимое фрагмента, поэтому передаем фрагмент в классы валидаторов
        final Validator validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(Spinner.class, new SpinnerValidatorAdapter()); //кастомный валидатор для списка языков

        //нажатие кнопки назад
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            }
        });
        //выход из учетной записи юзера
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //действия при нажатии кнопок диалогового окна
                DialogCallback dialogCallback = new DialogCallback() {
                    @Override
                    public void onPositiveButton() { //выходим из учетной записи
                        FirebaseNotificationService.unsubscribeFromNotifications(sharedPreferencesService.getLanguage()); //отписываемся от рассылки уведомлений
                        sharedPreferencesService.clearCredentials(); //удаляем из памяти инфу о юзере
                        //переходим на авторизацию
                        Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
                        startActivity(intent);
                        finishAffinity(); //закрываем активити профиля и главную
                    }

                    @Override
                    public void onNegativeButton() {
                        //ничего не делаем
                    }
                };
                DialogService.showDialog(getResources().getString(R.string.profile_logout), getResources().getString(R.string.profile_logout_confirm), getSupportFragmentManager(), dialogCallback);
            }
        });

        progressBar.show(); //включаем прогресс бар

        final GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                //получаем всю информацию о юзере с сервиса
                final UserDTO user = gsonService.fromJsonToObject(jsonBody, UserDTO.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //выключаем прогресс бар
                        progressBar.hide();
                        //заполняем поля
                        lastNameInput.setText(user.getLastName());
                        firstNameInput.setText(user.getFirstName());
                        middleNameInput.setText(user.getMiddleName());
                        formService.setSelectedGender(genderInput, user.getGender()); //устанавливаем пол radiobutton
                        formService.setSelectedLanguage(languageInput, getResources().getStringArray(R.array.languages_array_keys), user.getLanguage());
                        phoneNumberInput.setText(user.getPhoneNumber());
                    }
                });

            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.profile_get_error), message, getSupportFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.profile_get_error), message, getSupportFragmentManager());
            }
        };

        final String token = sharedPreferencesService.getToken();
        final String language = sharedPreferencesService.getLanguage();
        final String email = sharedPreferencesService.getEmail();
        requestService = new RequestService(sharedPreferencesService, new StartActivityService(this));
        requestService.doRequest("user/profile/", callback, token, language, "email", email);
    }

    //метод активации и деактивации полей формы
    private void switchFieldsEditable() {
        //отключаем/включаем все поля формы
        areFieldsEditable = !areFieldsEditable;
        for (int i = 0; i < formContainer.getChildCount(); i++) {
            View child = formContainer.getChildAt(i);
            child.setEnabled(areFieldsEditable);
        }
        //отображаем/скрываем элементы редактирования информации юзера
        int visibility = areFieldsEditable ? View.VISIBLE : View.GONE;
        formContainerEditElements.setVisibility(visibility);
    }

    //при успешной валидации
    @Override
    public void onValidationSucceeded() {
        progressBar.show(); //включаем прогресс бар
        saveButton.setEnabled(false); //выключаем кнопку сохранения во избежаение повторных запросов
        //отсылаем на сервис новую информацию о юзере
        final String token = sharedPreferencesService.getToken();
        String email = sharedPreferencesService.getEmail();
        final String currentPassword = formService.getTextFromInput(currentPasswordInput);
        String newPassword = formService.getTextFromInput(newPasswordInput);
        String firstName = formService.getTextFromInput(firstNameInput);
        String lastName = formService.getTextFromInput(lastNameInput);
        String middleName = formService.getTextFromInput(middleNameInput);
        String gender = formService.getSelectedGender(genderInput);
        final String language = formService.getSelectedLanguage(languageInput, getResources().getStringArray(R.array.languages_array_keys));
        final String oldLanguage = sharedPreferencesService.getLanguage();
        String phoneNumber = formService.getTextFromInput(phoneNumberInput);
        final UserDTO dto = new UserDTO(email, currentPassword, newPassword, firstName, lastName, middleName, gender, language, phoneNumber);

        final GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //очищаем поля с паролями
                        currentPasswordInput.setText("");
                        newPasswordInput.setText("");

                        progressBar.hide(); //выключаем прогресс бар
                        saveButton.setEnabled(true); //включаем кнопку сохранения
                        toastService.showToast(R.string.profile_save_success);
                        switchFieldsEditable(); //отключаем редактирование полей
                        sharedPreferencesService.setUser(dto); //запись в память новой информации о пользователе

                        //если поменялся язык
                        if (!language.equals(oldLanguage)) {
                            FirebaseNotificationService.unsubscribeFromNotifications(oldLanguage); //отписываемся от рассылки уведомлений на текущий язык
                            FirebaseNotificationService.subscribeToNotifications(language); //подписываемся на новый язык
                            LocaleService.setLocale(ProfileActivity.this, language); //установка нового языка приложения
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар и включаем кнопку сохранения
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //очищаем поля с паролями
                        currentPasswordInput.setText("");
                        newPasswordInput.setText("");
                        progressBar.hide();
                        saveButton.setEnabled(true);
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.profile_save_error), message, getSupportFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар и включаем кнопку сохранения
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //очищаем поля с паролями
                        currentPasswordInput.setText("");
                        newPasswordInput.setText("");
                        progressBar.hide();
                        saveButton.setEnabled(true);
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.profile_save_error), message, getSupportFragmentManager());
            }
        };

        final String json = gsonService.fromObjectToJson(dto);
        requestService.doPutRequest("user/edit", callback, token, language, json);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStop() {
        super.onStop();
        //при приостановке активити останавливаем все запросы
        requestService.cancelAllRequests();
    }
}
