package ru.tpu.russiantpu.main.fragments.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.dto.UserDTO;
import ru.tpu.russiantpu.main.activities.MainActivity;
import ru.tpu.russiantpu.utility.FormService;
import ru.tpu.russiantpu.utility.LocaleService;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.StartActivityService;
import ru.tpu.russiantpu.utility.ToastService;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.dialogFragmentServices.ErrorDialogService;
import ru.tpu.russiantpu.utility.notifications.FirebaseNotificationService;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;
import ru.tpu.russiantpu.utility.validation.SpinnerValidatorAdapter;
import ru.tpu.russiantpu.utility.validation.TextInputLayoutValidatorAdapter;

public class PersonalInfoFragment extends Fragment implements Validator.ValidationListener {

    @Pattern(regex = "^(?=.{0,50}$).*", messageResId = R.string.lastname_error) //optional, max 50
    private TextInputEditText lastNameInput;

    @NotEmpty(messageResId = R.string.empty_field_error)
    private TextInputEditText firstNameInput;

/*    private TextView groupInput;
    private List<String> groupNames = new ArrayList<>();*/

    //ввод пола не валидируется
    private Spinner genderInput;

    /*@NotEmpty(messageResId = R.string.empty_field_error)
    private Spinner languageInput; //список выбора языка
    private final List<LanguageDTO> languageDTOS = new ArrayList<>(); //пустой список заполнится после получение результата с сервиса*/

    @Pattern(regex = "(^$)|(^[+]\\d+$)", messageResId = R.string.phone_error) //optional, max 20
    private TextInputEditText phoneNumberInput;

    @Pattern(regex = "(^$)|(^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!&^%$#@_|\\/\\\\]{8,}$)", messageResId = R.string.password_error)
    private TextInputEditText currentPasswordInput;
    private TextInputLayout currentPasswordInputLayout;

    private TextView passwordRequiredText;

    @Pattern(regex = "(^$)|(^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!&^%$#@_|\\/\\\\]{8,}$)", messageResId = R.string.password_error)
    //пустая строка или regex пароля
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

    private boolean areFieldsEditable = false;

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
        LocaleService.setLocale(activity, sharedPreferencesService.getLanguageName());

        //все элементы формы в LinearLayout
        formContainer = layoutInflater.findViewById(R.id.profile_form);
        formContainerEditElements = formContainer.findViewById(R.id.profile_form_edit_elements);

        lastNameInput = formContainer.findViewById(R.id.input_lastname);
        firstNameInput = formContainer.findViewById(R.id.input_firstname);
        //groupInput = formContainer.findViewById(R.id.input_group_dialog);
        genderInput = formContainer.findViewById(R.id.input_gender_spinner);
        //languageInput = formContainer.findViewById(R.id.input_language_spinner);
        phoneNumberInput = formContainer.findViewById(R.id.input_phone_number);
        newPasswordInput = formContainer.findViewById(R.id.input_new_password);
        currentPasswordInput = formContainer.findViewById(R.id.input_current_password);
        currentPasswordInputLayout = formContainer.findViewById(R.id.input_current_password_layout);
        passwordRequiredText = formContainer.findViewById(R.id.password_required_text);
        saveButton = formContainer.findViewById(R.id.button_save);
        ImageButton editButton = activity.findViewById(R.id.button_edit);
        progressBar = layoutInflater.findViewById(R.id.progress_bar);

        // Отображаем поле "Текущий пароль", только если введен новый пароль
        newPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int visibility = s.length() > 0 ? View.VISIBLE : View.GONE;
                currentPasswordInputLayout.setVisibility(visibility);
                passwordRequiredText.setVisibility(visibility);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //устанавливаем все поля нередактируемыми
        enableEditableFields(false);

        //валидируем содержимое фрагмента, поэтому передаем фрагмент в классы валидаторов
        final Validator validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(Spinner.class, new SpinnerValidatorAdapter()); //кастомный валидатор для списка языков
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutValidatorAdapter());

        //при нажатии кнопки "сохранить изменения"
        saveButton.setOnClickListener(v -> validator.validate());

        //редактирование информации юзера
        editButton.setOnClickListener(v -> {
            //включаем/выключаем поля для редактирования
            areFieldsEditable = !areFieldsEditable;
            enableEditableFields(areFieldsEditable);
        });

        progressBar.show(); //включаем прогресс бар перед отправкой запросов

        //данные, необходимые для совершения запроса
        final String token = sharedPreferencesService.getToken();
        final String language = sharedPreferencesService.getLanguageId();
        final String email = sharedPreferencesService.getEmail();
        requestService = new RequestService(sharedPreferencesService, new StartActivityService(activity));

        /*//инициализация адаптера выбора языка
        final LanguagesAdapter languagesInputAdapter = new LanguagesAdapter(requireContext(), languageDTOS);
        languagesInputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageInput.setAdapter(languagesInputAdapter);

        //при нажатии на group input отображаем диалог со списком
        groupInput.setOnClickListener(view -> {
            //выводим выбранную группу через dialog fragment в text view
            SearchListDialogService.showDialog(R.layout.fragment_search_list, groupNames, getFragmentManager(),
                    selectedGroup -> groupInput.setText(selectedGroup));
        });*/

        // Делаем все запросы - получаем список языков, список групп, инфу о юзере
        doAllPersonalInfoRequests(token, language, email);

        // Также делаем все запросы повторно при свайпе вверх
        SwipeRefreshLayout swipeRefreshLayout = layoutInflater.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            doAllPersonalInfoRequests(token, language, email);
            swipeRefreshLayout.setRefreshing(false);
        });

        return layoutInflater;
    }

    private void doAllPersonalInfoRequests(String token, String languageId, String email) {
        //getLanguages(languageId);
        //getGroups(languageId);
        getUserProfile(languageId, email, token);
    }

    /*private void getLanguages(String language, LanguagesAdapter adapter) {
        //получение списка языков из бд
        requestService.doRequest("language", language, new GenericCallback<String>() {
            @Override
            public void onResponse(String json) {
                //полученные языки заносим в список (спиннер)
                languageDTOS.addAll(gsonService.fromJsonToArrayList(json, LanguageDTO.class));
                //уведомляем адаптер о получении языков
                getActivity().runOnUiThread(adapter::notifyDataSetChanged);
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар
                getActivity().runOnUiThread(() -> progressBar.hide());
                toastService.showToast(R.string.get_languages_error);
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар
                getActivity().runOnUiThread(() -> progressBar.hide());
                toastService.showToast(R.string.get_languages_error);
            }
        });
    }*/

    /*private void getGroups(String language) {
        //получение списка групп с сервиса
        requestService.doRequest("studyGroup", language, new GenericCallback<String>() {
            @Override
            public void onResponse(String json) {
                ArrayList<GroupsDTO> groupsDTO = gsonService.fromJsonToArrayList(json, GroupsDTO.class);
                groupNames.add(getResources().getString(R.string.group_none)); //первый элемент всегда "не указывать"
                for (GroupsDTO group : groupsDTO) {
                    groupNames.add(group.getName());
                }
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар
                getActivity().runOnUiThread(() -> progressBar.hide());
                toastService.showToast(message);
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар
                getActivity().runOnUiThread(() -> progressBar.hide());
                toastService.showToast(R.string.get_groups_error);
            }
        });
    }*/

    private void getUserProfile(String language, String email, String token) {

        final GenericCallback<String> getPersonalInfoCallback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                //получаем всю информацию о юзере с сервиса
                final UserDTO user = gsonService.fromJsonToObject(jsonBody, UserDTO.class);
                Log.d("", "");
                getActivity().runOnUiThread(() -> {
                    //выключаем прогресс бар
                    progressBar.hide();
                    //заполняем поля
                    lastNameInput.setText(user.getLastName());
                    firstNameInput.setText(user.getFirstName());
                    //groupInput.setText(formService.setGroup(user.getGroupName(), getResources().getString(R.string.dialog_none)));
                    formService.setSelectedGender(genderInput, user.getGender());
                    //formService.setSelectedLanguage(languageInput, user.getLanguageId(), languageDTOS);
                    phoneNumberInput.setText(user.getPhoneNumber());


/*                    PersonalInfoContent tpuPortalFragment = (PersonalInfoContent) getActivity().getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + 1);
                    tpuPortalFragment.onAcademicPlanUrlReceived(user.getAcademicPlanUrl());
                    tpuPortalFragment.onScheduleUrlReceived(user.getScheduleUrl());*/
                });

            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар
                getActivity().runOnUiThread(() -> progressBar.hide());
                toastService.showToast(message);
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар
                getActivity().runOnUiThread(() -> progressBar.hide());
                toastService.showToast(R.string.profile_get_error);
            }
        };

        requestService.doRequest("user/profile/", getPersonalInfoCallback, token, language, "email", email);
    }

    //метод активации и деактивации полей формы
    private void enableEditableFields(boolean enable) {
        //отключаем/включаем все поля формы
        //areFieldsEditable = !areFieldsEditable;
        for (int i = 0; i < formContainer.getChildCount(); i++) {
            View child = formContainer.getChildAt(i);
            child.setEnabled(enable);
        }
        //отображаем/скрываем элементы редактирования информации юзера
        int visibility = enable ? View.VISIBLE : View.GONE;
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
        final String groupName = "8В7Б";
        String gender = formService.getSelectedGender(genderInput);
        //final LanguageDTO languageDTO = null;
        final String languageId = "e06ed586-dd3b-4751-9bed-764047793afa";
        final String oldLanguageId = sharedPreferencesService.getLanguageId();
        final String oldLanguageShortName = sharedPreferencesService.getLanguageName();
        final String oldGroupName = sharedPreferencesService.getGroupName();
        String phoneNumber = formService.getTextFromInput(phoneNumberInput);
        final UserDTO dto = new UserDTO(email, currentPassword, newPassword, firstName, lastName,
                groupName, gender, languageId, "", phoneNumber);

        final GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String message) {
                activity.runOnUiThread(() -> {
                    //очищаем поля с паролями
                    currentPasswordInput.setText("");
                    newPasswordInput.setText("");

                    progressBar.hide(); //выключаем прогресс бар
                    saveButton.setEnabled(true); //включаем кнопку сохранения
                    toastService.showToast(R.string.profile_save_success);
                    enableEditableFields(false); //отключаем редактирование полей
                    sharedPreferencesService.setUser(dto); //запись в память новой информации о пользователе

                    //если поменялся язык
                    if (!languageId.equals(oldLanguageId)) {
                        FirebaseNotificationService.unsubscribeFromNotifications(oldLanguageShortName); //отписываемся от рассылки уведомлений на текущий язык
                        FirebaseNotificationService.subscribeToNotifications(""); //подписываемся на новый язык
                        LocaleService.setLocale(activity, ""); //установка нового языка приложения
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        String newGroupName = groupName == null ? "" : groupName; //новая группа, избегаем npe
                        if (!newGroupName.equals(oldGroupName)) { //если поменялась группа
                            //перезапускаем главное активити, чтобы заново загрузить меню 1 уровня и новую ссылку на расписание
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар и включаем кнопку сохранения
                activity.runOnUiThread(() -> {
                    //очищаем поля с паролями
                    currentPasswordInput.setText("");
                    newPasswordInput.setText("");
                    progressBar.hide();
                    saveButton.setEnabled(true);
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.profile_save_error), message, getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар и включаем кнопку сохранения
                activity.runOnUiThread(() -> {
                    //очищаем поля с паролями
                    currentPasswordInput.setText("");
                    newPasswordInput.setText("");
                    progressBar.hide();
                    saveButton.setEnabled(true);
                });
                toastService.showToast(R.string.profile_save_error);
            }
        };

        final String json = gsonService.fromObjectToJson(dto);
        requestService.doPutRequest("user/edit", callback, token, languageId, json);
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

    @Override
    public void onStop() {
        super.onStop();
        //при приостановке активити останавливаем все запросы
        requestService.cancelAllRequests();
    }
}
