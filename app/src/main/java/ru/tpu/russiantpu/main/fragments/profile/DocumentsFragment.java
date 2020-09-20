package ru.tpu.russiantpu.main.fragments.profile;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.dto.DocumentDTO;
import ru.tpu.russiantpu.main.dataAdapters.ClickListener;
import ru.tpu.russiantpu.main.dataAdapters.DocumentsDataAdapter;
import ru.tpu.russiantpu.utility.DownloadFileFromUrl;
import ru.tpu.russiantpu.utility.SharedPreferencesService;
import ru.tpu.russiantpu.utility.StartActivityService;
import ru.tpu.russiantpu.utility.callbacks.GenericCallback;
import ru.tpu.russiantpu.utility.dialogFragmentServices.ErrorDialogService;
import ru.tpu.russiantpu.utility.requests.GsonService;
import ru.tpu.russiantpu.utility.requests.RequestService;

public class DocumentsFragment extends Fragment {

    private RequestService requestService;
    private ContentLoadingProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        final FragmentManager fragmentManager = getFragmentManager();

        final View layoutInflater = inflater.inflate(R.layout.fragment_documents, container, false);
        final RecyclerView recyclerView = layoutInflater.findViewById(R.id.doc_list); //список
        progressBar = layoutInflater.findViewById(R.id.progress_bar);

        //вспомогательные классы
        final SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(activity);
        final GsonService gsonService = new GsonService();
        requestService = new RequestService(sharedPreferencesService, new StartActivityService(activity));

        final ArrayList<DocumentDTO> items = new ArrayList<>();
        final DocumentsDataAdapter adapter = new DocumentsDataAdapter(getContext(), items);

        //получение информации о юзере из sharedPreferences
        final String email = sharedPreferencesService.getEmail();
        final String token = sharedPreferencesService.getToken();
        final String language = sharedPreferencesService.getLanguage();

        adapter.setOnItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                String url = items.get(position).getUrl();
                String fileName = items.get(position).getFileName();
                DownloadFileFromUrl.downloadFile(url, fileName, token, activity);
            }
            //пока не используется, оставлен на будущее
            @Override
            public void onItemLongClick(int position, View v) {
            }
        });
        recyclerView.setAdapter(adapter);

        progressBar.show(); //включаем прогресс бар

        //реализация коллбека - что произойдет при получении данных с сервера
        GenericCallback<String> callback = new GenericCallback<String>() {
            @Override
            public void onResponse(String jsonBody) {
                items.addAll(gsonService.fromJsonToArrayList(jsonBody, DocumentDTO.class));

                //отрисовываем список статей в потоке интерфейса
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        progressBar.hide(); //выключаем прогресс бар
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
                ErrorDialogService.showDialog(getResources().getString(R.string.docs_get_error), message, fragmentManager);
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
                ErrorDialogService.showDialog(getResources().getString(R.string.docs_get_error), message, fragmentManager);
            }
        };

        //запрос за получение документов для пользователя (по email)
        requestService.doRequest("document", callback, token, language, "email", email);

        return layoutInflater;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //при закрытии фрагмента отменяем все запросы
        requestService.cancelAllRequests();
        //выключаем прогрессбар
        progressBar.hide();
    }
}
