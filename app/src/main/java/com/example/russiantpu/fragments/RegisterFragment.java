package com.example.russiantpu.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.russiantpu.R;
import com.example.russiantpu.dto.UserDTO;
import com.example.russiantpu.utility.ErrorDialogService;
import com.example.russiantpu.utility.GenericCallback;
import com.example.russiantpu.utility.GsonService;
import com.example.russiantpu.utility.RequestService;
import com.example.russiantpu.utility.SpinnerValidatorAdapter;
import com.example.russiantpu.utility.ToastService;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;
import java.util.Locale;

public class RegisterFragment extends Fragment implements Validator.ValidationListener {

    private final String fragmentTag = String.valueOf(R.string.prev_auth_frag_tag);

    //@NotEmpty(messageResId = R.string.empty_field_error)
    @Email(messageResId = R.string.email_error)
    private TextInputEditText emailInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    @Pattern(regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", messageResId = R.string.password_error)
    private TextInputEditText passwordInput;

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.lastname_error) //optional, max 50
    private TextInputEditText lastNameInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    private TextInputEditText firstNameInput;

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.middlename_error) //optional, max 50
    private TextInputEditText middleNameInput;

    private RadioGroup genderInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    private Spinner languageInput; //список выбора языка

    @Pattern(regex = "^(?=.{0,20}$).*", messageResId = R.string.phone_number) //optional, max 20
    private TextInputEditText phoneNumberInput;

    @Checked(messageResId = R.string.checkbox_error)
    private MaterialCheckBox checkBox; //согласие на обработку персональных данных

    private Button registerButton;

    private UserDTO dto = new UserDTO();
    private Context applicationContext;
    private RequestService requestService;
    private GsonService gsonService;
    private ToastService toastService;

    private String language;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ScrollView layoutInflater = (ScrollView)inflater.inflate(R.layout.fragment_register, container, false);
        applicationContext = getActivity().getApplicationContext();

        emailInput = layoutInflater.findViewById(R.id.input_email);
        passwordInput = layoutInflater.findViewById(R.id.input_password);
        firstNameInput = layoutInflater.findViewById(R.id.input_firstname);
        lastNameInput = layoutInflater.findViewById(R.id.input_lastname);
        middleNameInput = layoutInflater.findViewById(R.id.input_middlename);
        genderInput = layoutInflater.findViewById(R.id.input_gender);
        languageInput = layoutInflater.findViewById(R.id.input_language_spinner);
        phoneNumberInput = layoutInflater.findViewById(R.id.input_phone_number);
        checkBox = layoutInflater.findViewById(R.id.checkbox);
        registerButton = layoutInflater.findViewById(R.id.button_register);

        //валидируем содержимое фрагмента, поэтому передаем фрагмент в классы валидаторов
        final Validator validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(Spinner.class, new SpinnerValidatorAdapter()); //кастомный валидатор для списка языков

        //вспомогательные классы для отправки запроса
        requestService = new RequestService();
        gsonService = new GsonService();
        toastService = new ToastService(applicationContext);

        //получаем язык системы
        language = Locale.getDefault().getLanguage();

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
                validator.validate();
            }
        });

        return layoutInflater;
    }

    //если все поля пройдут валидацию
    @Override
    public void onValidationSucceeded() {
        toastService.showToast(R.string.validation_success);
        //берем выбраный пол, отсылаем всегда на английском
        String gender = null; //пол не обязательное поле, поэтому может быть null
        switch (genderInput.getCheckedRadioButtonId()) {
            case R.id.gender_male:
                gender = "Male";
                break;
            case R.id.gender_female:
                gender = "Female";
                break;
            default:
                break;
        }

        //берем выбранный язык
        String[] languagesKeys = getResources().getStringArray(R.array.languages_array_keys);
        String selectedLanguage = languagesKeys[languageInput.getSelectedItemPosition()];

        //заполняем дто регистрации, которое будем отправлять на сервис
        //если поля не заполнены, они равны null
        dto.updateFields(getTextFromInput(emailInput), getTextFromInput(passwordInput), getTextFromInput(firstNameInput),
                getTextFromInput(lastNameInput), getTextFromInput(middleNameInput), gender, selectedLanguage,
                getTextFromInput(phoneNumberInput));

        final GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                //нужно уведомить об успешной регистрации
                //нужно уведомить об процессе отправки запроса

                toastService.showToast(R.string.reg_success);

                // переход обратно на фрагмент логина при успешной регистрации
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LoginFragment()).addToBackStack(fragmentTag).commit();
            }

            @Override
            public void onError(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.register_error), gsonService.getFieldFromJson("message", message), getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.register_error), message, getFragmentManager());
            }
        };

        //если регистрация происходит через сторонние сервисы (поле != null), выбираем соответствующий юрл
        final String url = dto.getProvider() != null ? "auth/register/provider" : "auth/register";

/*        final String json = gsonService.fromObjectToJson(dto);
        requestService.doPostRequest(url, callback, json);*/

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String json = gsonService.fromObjectToJson(dto);
                requestService.doPostRequest(url, callback, language, json);
            }
        });

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(applicationContext);

            //отображение ошибки
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(message);
            }
            else {
                toastService.showToast(message);
            }
        }
    }

    //метод возвращает текст из EditText, если таковой имеется,
    //иначе возвращается null
    private String getTextFromInput(TextInputEditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")){
            return null;
        }
        else {
            return editText.getText().toString();
        }
    }
}
