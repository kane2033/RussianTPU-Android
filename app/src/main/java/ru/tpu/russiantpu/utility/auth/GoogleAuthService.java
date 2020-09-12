package ru.tpu.russiantpu.utility.auth;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import ru.tpu.russiantpu.R;

public class GoogleAuthService {

    private final int RC_SIGN_IN = 9001;
    private Fragment fragment;
    private GoogleSignInClient googleSignInClient;

    //по переменной будет проверяться, был ли вызван onActivityResult
    //с помощью этого класса
    public int getRC_SIGN_IN() {
        return RC_SIGN_IN;
    }

    public GoogleAuthService(Fragment fragment) {
        this.fragment = fragment;

        //конфигурация логина
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(fragment.getResources().getString(R.string.server_client_id_google))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(fragment.getActivity(), gso);
    }

    //метод запускает активити с логином через гугл
    public void initLogin() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        fragment.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //метод обрабатывает полученный результат логина из onActivityResult
    public String handleSignInResult(@NonNull Intent data) {
        try { //при успешной авторизации возвращаем айди токен
            Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            return account.getIdToken();
        } catch (ApiException e) {
            Log.w("AUTH", "handleSignInResult: error", e);
            return null;
        }
    }
}
