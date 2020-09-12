package ru.tpu.russiantpu.utility.notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseNotificationService {

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
