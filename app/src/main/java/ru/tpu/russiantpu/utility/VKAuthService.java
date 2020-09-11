package ru.tpu.russiantpu.utility;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class VKAuthService {

    private Fragment fragment;
    private static VKAuthCallback callback;

    public VKAuthService(Fragment fragment, final VKTokenCallback tokenCallback) {
        this.fragment = fragment;

        //инициализация коллбэка при логине
        callback = new VKAuthCallback() {
            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                //при успешном логине отправляем в коллбэк необходимые для авторизации на сервисе данные
                tokenCallback.onResponse(vkAccessToken.getAccessToken(), vkAccessToken.getUserId(), vkAccessToken.getEmail());
            }

            @Override
            public void onLoginFailed(int i) {
                Log.d("VK_ERR", "Error has occurred while attempting to login, code: " + i);
            }
        };
    }

    //метод запускает активити с логином
    public void initLogin() {
        FragmentActivity activity = fragment.getActivity();
        VK.login(activity, Arrays.asList(VKScope.EMAIL, VKScope.OFFLINE));
    }

    //метод вызывается в методе onActivityResult
    //метод статичен, так как необходимо сделать вызов в активити, потому что
    //VK SDK не поддерживает фрагменты
    public static boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return !VK.onActivityResult(requestCode, resultCode, data, callback);
    }
}
