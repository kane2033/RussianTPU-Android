package ru.tpu.russiantpu.main.fragments.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.ArrayList;
import java.util.List;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.dto.GroupsDTO;
import ru.tpu.russiantpu.dto.UserDTO;
import ru.tpu.russiantpu.main.activities.MainActivity;
import ru.tpu.russiantpu.utility.FormService;
import ru.tpu.russiantpu.utility.LocaleService;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.SpinnerValidatorAdapter;
import ru.tpu.russiantpu.utility.StartActivityService;
import ru.tpu.russiantpu.utility.ToastService;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.callbacks.ListDialogCallback;
import ru.tpu.russiantpu.utility.dialogFragmentServices.ErrorDialogService;
import ru.tpu.russiantpu.utility.dialogFragmentServices.SearchListDialogService;
import ru.tpu.russiantpu.utility.notifications.FirebaseNotificationService;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;

public class PersonalInfoFragment extends Fragment implements Validator.ValidationListener {

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.lastname_error) //optional, max 50
    private TextInputEditText lastNameInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    private TextInputEditText firstNameInput;

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.middlename_error) //optional, max 50
    private TextInputEditText middleNameInput;

    private TextView groupInput;
    private List<String> groupNames = new ArrayList<>();

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layoutInflater = inflater.inflate(R.layout.fragment_personal_info, container, false);
        final Activity activity = getActivity();

        //вспомогательные классы (сервисы)
        sharedPreferencesService = new SharedPreferencesService(activity);
        gsonService = new GsonService();
        formService = new FormService();
        toastService = new ToastService(activity);

        //установка языка интерфейса приложения
        LocaleService.setLocale(activity, sharedPreferencesService.getLanguage());

        //все элементы формы в LinearLayout
        formContainer = layoutInflater.findViewById(R.id.profile_form);
        formContainerEditElements = formContainer.findViewById(R.id.profile_form_edit_elements);

        lastNameInput = formContainer.findViewById(R.id.input_lastname);
        firstNameInput = formContainer.findViewById(R.id.input_firstname);
        middleNameInput = formContainer.findViewById(R.id.input_middlename);
        groupInput = formContainer.findViewById(R.id.input_group_dialog);
        genderInput = formContainer.findViewById(R.id.input_gender_spinner);
        languageInput = formContainer.findViewById(R.id.input_language_spinner);
        phoneNumberInput = formContainer.findViewById(R.id.input_phone_number);
        newPasswordInput = formContainer.findViewById(R.id.input_new_password);
        currentPasswordInput = formContainer.findViewById(R.id.input_current_password);
        saveButton = formContainer.findViewById(R.id.button_save);
        ImageButton editButton = activity.findViewById(R.id.button_edit);
        progressBar = layoutInflater.findViewById(R.id.progress_bar);

        //устанавливаем все поля нередактируемыми
        switchFieldsEditable();

        //валидируем содержимое фрагмента, поэтому передаем фрагмент в классы валидаторов
        final Validator validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(Spinner.class, new SpinnerValidatorAdapter()); //кастомный валидатор для списка языков

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

        progressBar.show(); //включаем прогресс бар перед отправкой запросов

        //данные, необходимые для совершения запроса
        final String token = sharedPreferencesService.getToken();
        final String language = sharedPreferencesService.getLanguage();
        final String email = sharedPreferencesService.getEmail();
        requestService = new RequestService(sharedPreferencesService, new StartActivityService(activity));

        //получение списка групп с сервиса
        requestService.doRequest("dicts/group", language, new GenericCallback<String>() {
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
                toastService.showToast(message);
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

        final GenericCallback<String> getPersonalInfoCallback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                //получаем всю информацию о юзере с сервиса
                final UserDTO user = gsonService.fromJsonToObject(jsonBody, UserDTO.class);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //выключаем прогресс бар
                        progressBar.hide();
                        //заполняем поля
                        lastNameInput.setText(user.getLastName());
                        firstNameInput.setText(user.getFirstName());
                        middleNameInput.setText(user.getMiddleName());
                        //если из полученного дто группа не указана (null), записываем строку "не указывать"/"none" вместо null
                        String groupName = user.getGroupName() == null ? getResources().getString(R.string.dialog_none) : user.getGroupName();
                        groupInput.setText(groupName);
                        formService.setSelectedGender(genderInput, user.getGender());
                        formService.setSelectedLanguage(languageInput, getResources().getStringArray(R.array.languages_array_keys), user.getLanguage());
                        phoneNumberInput.setText(user.getPhoneNumber());
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
                toastService.showToast(message);
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
                toastService.showToast(R.string.profile_get_error);
            }
        };

        requestService.doRequest("user/profile/", getPersonalInfoCallback, token, language, "email", email);

        return layoutInflater;
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
        final Activity activity = getActivity();
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
        final String groupName = formService.getGroup(groupInput, groupNames);
        String gender = formService.getSelectedGender(genderInput);
        final String language = formService.getSelectedLanguage(languageInput, getResources().getStringArray(R.array.languages_array_keys));
        final String oldLanguage = sharedPreferencesService.getLanguage();
        final String oldGroupName = sharedPreferencesService.getGroupName();
        String phoneNumber = formService.getTextFromInput(phoneNumberInput);
        final UserDTO dto = new UserDTO(email, currentPassword, newPassword, firstName, lastName, middleName, groupName, gender, language, phoneNumber);

        final GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String message) {
                activity.runOnUiThread(new Runnable() {
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
                            LocaleService.setLocale(activity, language); //установка нового языка приложения
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else {
                            String newGroupName = groupName == null ? "" : groupName; //новая группа, избегаем npe
                            if (!newGroupName.equals(oldGroupName)) { //если поменялась группа
                                //перезапускаем главное активити, чтобы заново загрузить меню 1 уровня и новую ссылку на расписание
                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар и включаем кнопку сохранения
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //очищаем поля с паролями
                        currentPasswordInput.setText("");
                        newPasswordInput.setText("");
                        progressBar.hide();
                        saveButton.setEnabled(true);
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.profile_save_error), message, getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар и включаем кнопку сохранения
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //очищаем поля с паролями
                        currentPasswordInput.setText("");
                        newPasswordInput.setText("");
                        progressBar.hide();
                        saveButton.setEnabled(true);
                    }
                });
                toastService.showToast(R.string.profile_save_error);
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
            String message = error.getCollatedErrorMessage(getActivity().getApplicationContext());

            //отображение ошибки
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(message);
            }
        }
    }

/*    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }*/

    @Override
    public void onStop() {
        super.onStop();
        //при приостановке активити останавливаем все запросы
        requestService.cancelAllRequests();
    }
}
