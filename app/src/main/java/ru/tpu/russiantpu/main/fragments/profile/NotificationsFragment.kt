package ru.tpu.russiantpu.main.fragments.profile

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.tpu.russiantpu.R
import ru.tpu.russiantpu.dto.NotificationDTO
import ru.tpu.russiantpu.main.dataAdapters.NotificationsDataAdapter
import ru.tpu.russiantpu.utility.SharedPreferencesService
import ru.tpu.russiantpu.utility.StartActivityService
import ru.tpu.russiantpu.utility.ToastService
import ru.tpu.russiantpu.utility.callbacks.GenericCallback
import ru.tpu.russiantpu.utility.requests.GsonService
import ru.tpu.russiantpu.utility.requests.RequestService

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private var requestService: RequestService? = null
    private var progressBar: ContentLoadingProgressBar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем скролл на самый вверх
        if (view is NestedScrollView) {
            view.setFocusableInTouchMode(true)
            view.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        }

        //вспомогательные классы
        val sharedPreferencesService = SharedPreferencesService(activity)
        val gsonService = GsonService()
        val toastService = ToastService(context)
        requestService = RequestService(sharedPreferencesService, StartActivityService(activity))

        // инициализация RecyclerView с адаптером
        val docsRecyclerView: RecyclerView = view.findViewById(R.id.notif_list)
        val adapter = NotificationsDataAdapter() // Не устанавливаем clickListener
        docsRecyclerView.adapter = adapter

        progressBar = view.findViewById(R.id.progress_bar)

        //получение информации о юзере из sharedPreferences
        val email = sharedPreferencesService.email
        val language = sharedPreferencesService.languageId
        val token = sharedPreferencesService.token

        // Делаем запрос на получение всех уведомлений
        getNotifications(email, token, language, gsonService, adapter, toastService)

        // Также делаем запрос повторно при свайпе вверх
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        refreshLayout?.setOnRefreshListener {
            getNotifications(email, token, language, gsonService, adapter, toastService)
            refreshLayout.isRefreshing = false
        }
    }

    private fun getNotifications(email: String, token: String, language: String,
                                 gsonService: GsonService,
                                 adapter: NotificationsDataAdapter, toastService: ToastService) {
        progressBar?.show() //включаем прогресс бар
        //реализация коллбека - что произойдет при получении данных с сервера
        val callback: GenericCallback<String> = object : GenericCallback<String> {
            override fun onResponse(jsonBody: String) {
                val notifications = gsonService.fromJsonToArrayList(jsonBody, NotificationDTO::class.java)

                activity?.runOnUiThread {
                    progressBar?.hide() //выключаем прогресс бар
                    adapter.updateList(notifications)
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
        requestService?.doRequest("notification", callback, token, language, "email", email)
    }

    override fun onDetach() {
        super.onDetach()
        //при закрытии фрагмента отменяем все запросы
        requestService?.cancelAllRequests()
        //выключаем прогрессбар
        progressBar?.hide()
    }
}