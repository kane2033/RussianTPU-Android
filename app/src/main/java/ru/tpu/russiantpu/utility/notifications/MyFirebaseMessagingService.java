package ru.tpu.russiantpu.utility.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ru.tpu.russiantpu.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String notificationsId = "firebase";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.w("FIREBASE_TOKEN", "token: " + token);
    }

    //коллбэк при получении уведомления
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(),
                remoteMessage.getData().get(NotificationResolver.APP_LINK_KEY)
        );
    }

    //метод отображает полученные от firebase уведомления
    //в случае, если приложение открыто и не находится в бэкграунде
    private void sendNotification(String messageTitle, String messageBody, String linkTo) {
/*        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //API >= 26 требует создания канала уведомлений
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(notificationsId,
                    getResources().getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getResources().getString(R.string.notification_channel_description));
            notificationManager.createNotificationChannel(channel);
        }

        PendingIntent intent = NotificationResolver.INSTANCE.getPendingIntent(linkTo, this);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, notificationsId)
                .setSmallIcon(R.drawable.ic_tpu_logo_aya_notification)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_tpu))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        notificationManager.notify(0, notificationBuilder.build());
    }
}
