package ru.tpu.russiantpu.utility;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import ru.tpu.russiantpu.R;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Класс с методом, ответственным за скачивание файлов в папку download по url
 * */
public class DownloadFileFromUrl {

    public static void downloadFile(String url, String fileName, String token, Fragment fragment) {
        if (isStoragePermissionGranted(fragment)) { //проверяем, есть ли разрешение на скачку файлов
            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                        .addRequestHeader("Authorization", "Bearer " + token) //JWT
                        .setTitle(fileName)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                        .setAllowedOverRoaming(true);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    request.setRequiresCharging(false)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true);
                }
                DownloadManager downloadManager = (DownloadManager) fragment.getActivity().getSystemService(DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
            } catch (Exception e) { //отображаем сообщение об ошибке при неудачной скачке
                e.printStackTrace();
                Toast.makeText(fragment.getContext(), R.string.docs_download_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //метод проверки на наличие права на сохранение файлов
    private static  boolean isStoragePermissionGranted(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //если версия андройд >= 6.0
            //если разрешение на сохранение уже есть
            if (fragment.getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else {
                //иначе запрашиваем разрешение через диалоговое окно
                fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                //ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //на андройд < 6.0 проблемы нет
            return true;
        }
    }
}
