package ru.tpu.russiantpu.utility;

import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Класс с методом, ответственным за скачивание файлов в папку download по url
 * */
public class DownloadFileFromUrl {

    public static void downloadFile(String url, String fileName, String token, Activity activity) {
        //сохраняем в папку download
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .addRequestHeader("Authorization", "Bearer " + token) //JWT
                .setTitle(fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(file))
                .setAllowedOverRoaming(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            request.setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
        }
        DownloadManager downloadManager = (DownloadManager)activity.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
}
