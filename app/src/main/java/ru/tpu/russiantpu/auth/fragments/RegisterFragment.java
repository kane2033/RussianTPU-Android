package ru.tpu.russiantpu.auth.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.dto.GroupsDTO;
import ru.tpu.russiantpu.dto.LanguageDTO;
import ru.tpu.russiantpu.dto.UserDTO;
import ru.tpu.russiantpu.utility.FormService;
import ru.tpu.russiantpu.utility.SpinnerValidatorAdapter;
import ru.tpu.russiantpu.utility.ToastService;
import ru.tpu.russiantpu.utility.adapters.LanguagesAdapter;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.callbacks.ListDialogCallback;
import ru.tpu.russiantpu.utility.dialogFragmentServices.ErrorDialogService;
import ru.tpu.russiantpu.utility.dialogFragmentServices.SearchListDialogService;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;

public class RegisterFragment extends Fragment implements Validator.ValidationListener {

    private final String fragmentTag = String.valueOf(R.string.prev_auth_frag_tag);

    //@NotEmpty(messageResId = R.string.empty_field_error)
    @Email(messageResId = R.string.email_error)
    private TextInputEditText emailInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    @Pattern(regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!&^%$#@_|\\/\\\\]{8,}$", messageResId = R.string.password_error)
    private TextInputEditText passwordInput;

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.lastname_error) //optional, max 50
    private TextInputEditText lastNameInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    private TextInputEditText firstNameInput;

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.middlename_error) //optional, max 50
    private TextInputEditText middleNameInput;

    private List<String> groupNames = new ArrayList<>();
    private TextView groupInput;

    private Spinner genderInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    private Spinner languageInput; //список выбора языка

    @Pattern(regex = "^(?=.{0,20}$).*", messageResId = R.string.phone_number) //optional, max 20
    private TextInputEditText phoneNumberInput;

    @Checked(messageResId = R.string.checkbox_error)
    private MaterialCheckBox checkBox; //согласие на обработку персональных данных

    private Button registerButton;
    private ContentLoadingProgressBar progressBar;

    private UserDTO dto = new UserDTO();
    private RequestService requestService;
    private GsonService gsonService;
    private ToastService toastService;

    private String language;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ScrollView layoutInflater = (ScrollView)inflater.inflate(R.layout.fragment_register, container, false);
        final Activity activity = getActivity();
        Context applicationContext = activity.getApplicationContext();

        emailInput = layoutInflater.findViewById(R.id.input_email);
        passwordInput = layoutInflater.findViewById(R.id.input_password);
        firstNameInput = layoutInflater.findViewById(R.id.input_firstname);
        lastNameInput = layoutInflater.findViewById(R.id.input_lastname);
        middleNameInput = layoutInflater.findViewById(R.id.input_middlename);
        groupInput = layoutInflater.findViewById(R.id.input_group_dialog);
        genderInput = layoutInflater.findViewById(R.id.input_gender_spinner);
        languageInput = layoutInflater.findViewById(R.id.input_language_spinner);
        phoneNumberInput = layoutInflater.findViewById(R.id.input_phone_number);
        checkBox = layoutInflater.findViewById(R.id.checkbox);
        registerButton = layoutInflater.findViewById(R.id.button_register);
        progressBar = layoutInflater.findViewById(R.id.progress_bar);

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

        //получение списка групп с сервиса
        requestService.doRequest("dict/group", language, new GenericCallback<String>() {
            @Override
            public void onResponse(String json) {
                ArrayList<GroupsDTO> groupsDTO = gsonService.fromJsonToArrayList(json, GroupsDTO.class);
                groupNames.add(getResources().getString(R.string.group_none)); //первый элемент всегда "не указывать"
                for (GroupsDTO group : groupsDTO) {
                    groupNames.add(group.getGroupName());
                }
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                    }
                });
                toastService.showToast(R.string.get_groups_error);
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                    }
                });
                toastService.showToast(R.string.get_groups_error);
            }
        });

        //при нажатии на group input отображаем диалог со списком
        groupInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //выводим выбранную группу через dialog fragment в text view
                SearchListDialogService.showDialog(R.layout.fragment_search_list, groupNames, getFragmentManager(), new ListDialogCallback() {
                    @Override
                    public void onItemClick(String selectedGroup) {
                        groupInput.setText(selectedGroup);
                    }
                });
            }
        });

        //инициализация адаптера выбора языка
        final List<LanguageDTO> languageDTOS = new ArrayList<>(); //пустой список заполнится после получение результата с сервиса
        final LanguagesAdapter languagesInputAdapter = new LanguagesAdapter(requireContext(), languageDTOS);
        languagesInputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageInput.setAdapter(languagesInputAdapter);

        //получение списка языков из бд
        requestService.doRequest("language", language, new GenericCallback<String>() {
            @Override
            public void onResponse(String json) {
                //полученные языки заносим в список (спиннер)
                languageDTOS.addAll(gsonService.fromJsonToArrayList(json, LanguageDTO.class));
                //уведомляем адаптер о получении языков
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        languagesInputAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                    }
                });
                toastService.showToast(R.string.get_languages_error);
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                    }
                });
                toastService.showToast(R.string.get_languages_error);
            }
        });

        //если юзер регистрируется после авторизации
        //через сторонний сервис, заполняем имеющиеся поля
        if (getArguments() != null) {
            dto = getArguments().getParcelable("registerDTO");
            emailInput.setText(dto.getEmail());
            firstNameInput.setText(dto.getFirstName());
            lastNameInput.setText(dto.getLastName());

            //запрещаем редактировать уже заполненные поля
            emailInput.setEnabled(false);

            //уведомляем юзера, что нужно дозаполнить поля
            TextView textView = layoutInflater.findViewById(R.id.reg_text);
            textView.setText(R.string.reg_text_provider);
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
        final Activity activity = getActivity();
        FormService formService = new FormService(); //класс берет значения из полей формы
        progressBar.show(); //включаем прогресс бар
        registerButton.setEnabled(false); //отключаем кнопку регистрации для избежания повторных запросов
        toastService.showToast(R.string.validation_success);

        //все поля формы
        String email = formService.getTextFromInput(emailInput);
        String password = formService.getTextFromInput(passwordInput);
        String firstName = formService.getTextFromInput(firstNameInput);
        String lastName = formService.getTextFromInput(lastNameInput);
        String middleName = formService.getTextFromInput(middleNameInput);
        String groupName = formService.getGroup(groupInput, groupNames);
        String gender = formService.getSelectedGender(genderInput); //пол не обязательное поле, поэтому может быть null
        String selectedLanguage = formService.getSelectedLanguage(languageInput).getId();
        String phoneNumber = formService.getTextFromInput(phoneNumberInput);

        //заполняем дто регистрации, которое будем отправлять на сервис
        //если поля не заполнены, они равны null
        dto.updateFields(email, password, firstName, lastName, middleName, groupName, gender, selectedLanguage, phoneNumber);

        final GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                //выключаем прогресс бар и включаем кнопку регистрации
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                        registerButton.setEnabled(true);
                    }
                });
                toastService.showToast(R.string.reg_success);

                // переход обратно на фрагмент логина при успешной регистрации
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LoginFragment()).addToBackStack(fragmentTag).commit();
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар и включаем кнопку регистрации
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                        registerButton.setEnabled(true);
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.register_error), message, getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар и включаем кнопку регистрации
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                        registerButton.setEnabled(true);
                    }
                });
                toastService.showToast(R.string.register_error);
            }
        };

        //если регистрация происходит через сторонние сервисы (поле != null), выбираем соответствующий юрл
        final String url = dto.getProvider() != null ? "auth/provider/registration" : "auth/local/registration";

        activity.runOnUiThread(new Runnable() {
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
            String message = error.getCollatedErrorMessage(getActivity().getApplicationContext());

            //отображение ошибки
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(message);
            }
            else {
                toastService.showToast(message);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //при закрытии фрагмента отменяем все запросы
        requestService.cancelAllRequests();
    }
}
