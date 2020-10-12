package ru.tpu.russiantpu.utility.notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import ru.tpu.russiantpu.dto.FirebaseDTO;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;

/**
* Класс, ответственный за подписку и отписку от уведомлений на реализации Firebase.
 * Существует два типа - подписка по токену пользователя и по языку (топику)
* */
public class FirebaseNotificationService {

    //метод подписки пользователя по токену firebase - отсылается регистрация на сервис
    public static void subscribeUserToNotifications(final RequestService requestService, final String email, final String language) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                //получение токена firebase
                final String token = instanceIdResult.getToken();
                Log.w("FIREBASE_TOKEN", "token: " + token);

                //создаем json пользователя для отправки на сервис
                final GsonService gsonService = new GsonService();
                final String json = gsonService.fromObjectToJson(new FirebaseDTO(email, token));

                GenericCallback<String> callback = new GenericCallback<String>() {
                    @Override
                    public void onResponse(String value) {
                        Log.d("FIREBASE_SUB", "user successfully subscribed");
                    }

                    @Override
                    public void onError(String value) {
                        Log.d("FIREBASE_SUB", "subscription failed - http error");
                    }

                    @Override
                    public void onFailure(String value) {
                        Log.d("FIREBASE_SUB", "subscription failed - internal exception");
                    }
                };

                requestService.doPostRequest("auth/fcmToken/save", callback, language, json);
            }
        });
    }

    //метод отписки пользователя по токену firebase - отсылается запрос на сервис
    public static void unsubscribeUserFromNotifications(final RequestService requestService, final String email, final String language) {
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String value) {
                Log.d("FIREBASE_SUB", "user successfully unsubscribed");
            }

            @Override
            public void onError(String value) {
                Log.d("FIREBASE_SUB", "unsubscription failed - http error");
            }

            @Override
            public void onFailure(String value) {
                Log.d("FIREBASE_SUB", "unsubscription failed - internal exception");
            }
        };

        requestService.doPostRequest("auth/fcmToken/disable", callback, language, "email", email);
    }

    //метод подписки приложения пользователя по языку (топику)
    public static void subscribeToNotifications(String language) {
        FirebaseMessaging.getInstance().subscribeToTopic("news_" + language)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("FIREBASE_SUB", "successfully subscribed");
                        }
                        else {
                            Log.d("FIREBASE_SUB", "subscription failed");
                        }
                    }
                });
    }

    //метод отписки приложения пользователя по языку (топику)
    public static void unsubscribeFromNotifications(String language) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("news_" + language)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("FIREBASE_UNSUB", "successfully unsubscribed");
                        }
                        else {
                            Log.d("FIREBASE_UNSUB", "failed while trying to unsubscribe");
                        }
                    }
                });
    }
}
