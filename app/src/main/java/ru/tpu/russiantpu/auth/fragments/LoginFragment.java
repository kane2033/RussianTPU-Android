package ru.tpu.russiantpu.auth.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;
import java.util.Locale;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.dto.LoginByProviderDTO;
import ru.tpu.russiantpu.dto.LoginDTO;
import ru.tpu.russiantpu.dto.TokensDTO;
import ru.tpu.russiantpu.dto.UserDTO;
import ru.tpu.russiantpu.main.activities.MainActivity;
import ru.tpu.russiantpu.utility.AuthService;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.ToastService;
import ru.tpu.russiantpu.utility.auth.FacebookAuthService;
import ru.tpu.russiantpu.utility.auth.GoogleAuthService;
import ru.tpu.russiantpu.utility.auth.VKAuthService;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.callbacks.VKTokenCallback;
import ru.tpu.russiantpu.utility.dialogFragmentServices.ErrorDialogService;
import ru.tpu.russiantpu.utility.dialogFragmentServices.ResetPasswordDialogService;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;

public class LoginFragment extends Fragment implements View.OnClickListener, Validator.ValidationListener {

    private final String fragmentTag = String.valueOf(R.string.prev_auth_frag_tag);

    @Email(messageResId = R.string.email_error)
    private EditText emailInput;
    @NotEmpty(messageResId = R.string.empty_field_error)
    private EditText passwordInput;

    private TextView resetPassword;
    private MaterialCheckBox rememberMeCheckBox;
    private Button loginButton;
    private ImageView loginGoogle;
    private ImageView loginFacebook;
    private ImageView loginVK;
    private ContentLoadingProgressBar progressBar;

    private RequestService requestService;
    private GsonService gsonService;
    private GoogleAuthService googleAuth;
    private FacebookAuthService fbAuth;
    private VKAuthService vkAuth;

    private GenericCallback<String> toMainActivityCallback;
    private GenericCallback<String> providerAuthCallback;
    private Validator validator;

    private String language;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ScrollView layoutInflater = (ScrollView)inflater.inflate(R.layout.fragment_login, container, false);
        final Activity activity = getActivity();

        emailInput = layoutInflater.findViewById(R.id.input_email);
        passwordInput = layoutInflater.findViewById(R.id.input_password);
        rememberMeCheckBox = layoutInflater.findViewById(R.id.remember_me_checkbox);
        resetPassword = layoutInflater.findViewById(R.id.reset_password);
        loginButton = layoutInflater.findViewById(R.id.button_login);
        loginGoogle = layoutInflater.findViewById(R.id.button_login_google);
        loginFacebook = layoutInflater.findViewById(R.id.button_login_facebook);
        loginVK = layoutInflater.findViewById(R.id.button_login_vk);
        progressBar = layoutInflater.findViewById(R.id.progress_bar);

        loginButton.setOnClickListener(this);
        loginGoogle.setOnClickListener(this);
        loginFacebook.setOnClickListener(this);
        loginVK.setOnClickListener(this);

        requestService = new RequestService();
        gsonService = new GsonService();
        final ToastService toastService = new ToastService(getContext());

        //валидируем содержимое фрагмента, поэтому передаем фрагмент в классы валидаторов
        validator = new Validator(this);
        validator.setValidationListener(this);

        //получаем язык устройства
        language = Locale.getDefault().getLanguage();

        //инициализация коллбэка
        // при нажатии кнопки логина через сервис
        GenericCallback<String> getTokenCallback = new GenericCallback<String>() {
            @Override
            public void onResponse(String token) {
                sendIdTokenToService(token, getResources().getString(R.string.provider_facebook));
            }

            @Override
            public void onError(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.login_error), message, getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                toastService.showToast(R.string.login_error);
            }
        };

        //специальный коллбэк для VK, потому что требуется отослать
        //дополнительно email и userId
        VKTokenCallback getVKTokenCallback = new VKTokenCallback() {
            @Override
            public void onResponse(String token, Integer userId, String email) {
                sendIdTokenToService(token, userId.toString(), email, getResources().getString(R.string.provider_vk));
            }
        };

        //создаем классы, осуществляющие авторизацию через сервисы
        googleAuth = new GoogleAuthService(this);
        fbAuth = new FacebookAuthService(this, getTokenCallback);
        vkAuth = new VKAuthService(this, getVKTokenCallback);

        //коллбэк при успешном запросе - переход в основное активити
        toMainActivityCallback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                //выключаем прогресс бар, включаем кнопки
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                        enableButtons(true);
                    }
                });

                //получение токенов с сервиса
                TokensDTO tokens = gsonService.fromJsonToObject(jsonBody, TokensDTO.class);

                SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(activity);
                // Подпись и сохранение токена
                AuthService.INSTANCE.login(tokens, requestService, sharedPreferencesService);

                //очистка фрагментов из стека при переходе в основную активити - в противном случае, при нажатии кнопки "назад"
                //происходит непредвиденный переход в логин
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                Intent intent = new Intent(getContext(), MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }

            @Override
            public void onError(String message) {
                //выключаем прогресс бар, включаем кнопки
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                        enableButtons(true);
                    }
                });
                ErrorDialogService.showDialog(getResources().getString(R.string.login_error), message, getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                //выключаем прогресс бар, включаем кнопки
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();
                        enableButtons(true);
                    }
                });
                toastService.showToast(R.string.login_error);
            }
        };

        //коллбэк, когда при авторизации через сторонние сервисы
        //оказывается, что пользователь не зарегистрирован в бд
        providerAuthCallback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                if (requestService.getResponseCode() == 210) { //если пользователь отсутствует в бд
                    //получаем данные пользователя, вошедшего через сторонний сервис
                    UserDTO dto = gsonService.fromJsonToObject(jsonBody, UserDTO.class);

                    //переносим полученные данные пользователя в фрагмент регистрации
                    Bundle args = new Bundle();
                    args.putParcelable("registerDTO", dto);
                    RegisterFragment fragment = new RegisterFragment();
                    fragment.setArguments(args);

                    //переходим к фрагменту регистрации
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            fragment).addToBackStack(fragmentTag).commit();
                }
                else { //код 200
                    toMainActivityCallback.onResponse(jsonBody); //обычный коллбэк, при котором переходим в приложение
                }
            }

            @Override
            public void onError(String message) {
                ErrorDialogService.showDialog(getResources().getString(R.string.login_error), message, getFragmentManager());
            }

            @Override
            public void onFailure(String message) {
                toastService.showToast(R.string.login_error);
            }
        };

        //восстановление пароля
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPasswordDialogService.showDialog(emailInput.getText().toString(), language, requestService, getFragmentManager());
            }
        });

        return layoutInflater;
    }

    //метод отправляет переданный токен с указанным провайдером на сервис
    private void sendIdTokenToService(String idToken, String provider) {
        //отправляет токен google на сервис для авторизации
        String json = gsonService.fromObjectToJson(new LoginByProviderDTO(provider, idToken));
        requestService.doPostRequest("auth/provider/login", providerAuthCallback, language, json);
    }

    //метод для ВК - с токеном необходимо отправить userId и email
    private void sendIdTokenToService(String idToken, String userId, String email, String provider) {
        //отправляет токен google на сервис для авторизации
        String json = gsonService.fromObjectToJson(new LoginByProviderDTO(provider, idToken, userId,email));
        requestService.doPostRequest("auth/provider/login", providerAuthCallback, language, json);
    }

    //метод включает/отключает кнопки формы при отправке данных на сервис
    //для избежания повторных запросов
    private void enableButtons(boolean enable) {
        loginButton.setEnabled(enable);
        loginGoogle.setEnabled(enable);
        loginFacebook.setEnabled(enable);
        loginVK.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                //валидируем поля email и пароль,
                //результат в onValidationSucceeded, onValidationFailed
                validator.validate();
                break;
            case R.id.button_login_google:
                //инициализируем логин через google,
                //авторизация продолжается в onActivityResult
                googleAuth.initLogin();
                break;
            case R.id.button_login_facebook:
                fbAuth.initLogin();
                break;
            case R.id.button_login_vk:
                vkAuth.initLogin();
                break;
            default:
                break;
        }
    }

    //после авторизации через активити стороннего сервиса
    //вызывается этот метод, в котором берется idToken и
    //отсылкается на сервис
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //получение айди токена google при успешной авторизации
        if (requestCode == googleAuth.getRC_SIGN_IN()) {
            String idToken = googleAuth.handleSignInResult(data);
            if (idToken != null) {
                //отправляет токен google на сервис для авторизации
                sendIdTokenToService(idToken, getResources().getString(R.string.provider_google));
            }
        }

        //менеджер коллбэка логина фб получает результаты логина
        fbAuth.getCallbackManager().onActivityResult(requestCode, resultCode, data);

        //!!! vkAuth.onActivityResult() используется в AuthActivity, потому что SDK VK
        //не поддерживает фрагменты
    }

    @Override
    public void onValidationSucceeded() {
        progressBar.show(); //включаем прогресс бар
        enableButtons(false); //выключаем все кнопки
        String json = gsonService.fromObjectToJson(new LoginDTO(emailInput.getText().toString(), passwordInput.getText().toString(), rememberMeCheckBox.isChecked()));
        requestService.doPostRequest("auth/local/login", toMainActivityCallback, language, json);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            //отображение ошибки
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(message);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //при закрытии фрагмента отменяем все запросы
        if (requestService != null) {
            requestService.cancelAllRequests();
        }
    }
}
