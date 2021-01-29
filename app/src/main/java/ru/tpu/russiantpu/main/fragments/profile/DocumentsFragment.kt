package ru.tpu.russiantpu.main.fragments.profile

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.tpu.russiantpu.R
import ru.tpu.russiantpu.dto.DocumentDTO
import ru.tpu.russiantpu.main.dataAdapters.DocumentsDataAdapter
import ru.tpu.russiantpu.utility.DownloadFileFromUrl
import ru.tpu.russiantpu.utility.SharedPreferencesService
import ru.tpu.russiantpu.utility.StartActivityService
import ru.tpu.russiantpu.utility.ToastService
import ru.tpu.russiantpu.utility.callbacks.GenericCallback
import ru.tpu.russiantpu.utility.requests.GsonService
import ru.tpu.russiantpu.utility.requests.RequestService
import java.util.*

class DocumentsFragment : Fragment(R.layout.fragment_documents) {
    private var requestService: RequestService? = null
    private var progressBar: ContentLoadingProgressBar? = null

    //переменные файла
    private var url = "" //ссылка на скачивание
    private var fileName = "" //название файла
    private var token = ""//токен для получения файла

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Без этой надстройки скрывается выбор списка документов
        // ("Новые документы", "просмотренные документы")
        if (view is NestedScrollView) {
            view.setFocusableInTouchMode(true)
            view.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS;
        }

        //вспомогательные классы
        val sharedPreferencesService = SharedPreferencesService(activity)
        val gsonService = GsonService()
        val toastService = ToastService(context)
        requestService = RequestService(sharedPreferencesService, StartActivityService(activity))

        //получение информации о юзере из sharedPreferences
        val email = sharedPreferencesService.email
        val language = sharedPreferencesService.languageId
        token = sharedPreferencesService.token

        // инициализация RecyclerView с адаптером
        val docsRecyclerView: RecyclerView = view.findViewById(R.id.doc_list)
        val adapter = DocumentsDataAdapter(clickListener = { document ->
            url = document.url
            fileName = document.fileName
            DownloadFileFromUrl.downloadFile(url, fileName, token, this@DocumentsFragment)
        })
        docsRecyclerView.adapter = adapter

        val docsSelector: Spinner = view.findViewById(R.id.docs_selector)
        docsSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> adapter.showNewDocuments()
                    1 -> adapter.showSeenDocuments()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ничего не делаем
            }
        }

        progressBar = view.findViewById(R.id.progress_bar)
        progressBar?.show() //включаем прогресс бар

        //реализация коллбека - что произойдет при получении данных с сервера
        val callback: GenericCallback<String> = object : GenericCallback<String> {
            override fun onResponse(jsonBody: String) {
                val docs = gsonService.fromJsonToArrayList(jsonBody, DocumentDTO::class.java)

                activity?.runOnUiThread {
                    progressBar?.hide() //выключаем прогресс бар
                    adapter.updateList(docs)
                }
            }

            override fun onError(message: String) {
                //выключаем прогресс бар
                activity?.runOnUiThread { progressBar?.hide() }
                toastService.showToast(message)
            }

            override fun onFailure(message: String) {
                //выключаем прогресс бар
                activity?.runOnUiThread { progressBar?.hide() }
                toastService.showToast(R.string.docs_get_error)
            }
        }

        //запрос за получение документов для пользователя (по email)
        requestService?.doRequest("document", callback, token, language, "email", email)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //если юзер дал разрешение на сохранение файла через диалоговое окно, пытаемся скачать файл повторно
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            DownloadFileFromUrl.downloadFile(url, fileName, token, this@DocumentsFragment)
        }
    }

    override fun onDetach() {
        super.onDetach()
        //при закрытии фрагмента отменяем все запросы
        requestService?.cancelAllRequests()
        //выключаем прогрессбар
        progressBar?.hide()
    }
}