package ru.tpu.russiantpu.main.fragments.calendar

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import ru.tpu.russiantpu.R
import ru.tpu.russiantpu.dto.EventDetailsDTO
import ru.tpu.russiantpu.utility.LocaleService
import ru.tpu.russiantpu.utility.SharedPreferencesService
import ru.tpu.russiantpu.utility.StartActivityService
import ru.tpu.russiantpu.utility.ToastService
import ru.tpu.russiantpu.utility.callbacks.GenericCallback
import ru.tpu.russiantpu.utility.requests.GsonService
import ru.tpu.russiantpu.utility.requests.RequestService

class CalendarDetailsFragment : Fragment(R.layout.fragment_calendar_details) {

    private var progressBar: ContentLoadingProgressBar? = null
    private var requestService: RequestService? = null
    private var eventDetails: EventDetailsDTO? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

/*        (requireActivity() as? AppCompatActivity)?.let {
            it.supportActionBar?.setTitle(R.string.calendar_title_details)
        }*/

        val sharedPreferencesService = SharedPreferencesService(requireActivity())
        //установка языка приложения (интерфейса)
        LocaleService.setLocale(requireActivity(), sharedPreferencesService.languageName)

        progressBar = requireActivity().findViewById(R.id.progress_bar)
        progressBar?.show()

        // Кнопка "Участвовать"
        val participateButton = view.findViewById(R.id.participate_button) as Button
        participateButton.setOnClickListener {
            eventDetails?.onlineMeetingLink?.let {
                activity?.openLink(it)
            }
        }

        val webView = view.findViewById(R.id.detailed_message) as WebView
        webView.settings.javaScriptEnabled = true

        //вспомогательные классы
        val gsonService = GsonService()
        val toastService = ToastService(context)
        requestService = RequestService(sharedPreferencesService, StartActivityService(activity))

        val eventId = arguments?.getString("eventId") ?: ""

        requestService?.let {
            it.doRequest("calendarEvent/$eventId/detailed", object : GenericCallback<String> {
                override fun onResponse(json: String?) {
                    eventDetails = gsonService.fromJsonToObject(json, EventDetailsDTO::class.java)

                    requireActivity().runOnUiThread {
                        progressBar?.hide()

                        val message = eventDetails?.detailedMessage
                                ?: getString(R.string.calendar_event_details_missing)
                        webView.loadDataWithBaseURL("https://internationals.tpu.ru:8080",
                                message, "text/html", "UTF-8", null)

                        eventDetails?.onlineMeetingLink?.let {
                            participateButton.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onError(message: String?) {
                    //выключаем прогресс бар
                    requireActivity().runOnUiThread { progressBar?.hide() }
                    toastService.showToast(message)
                }

                override fun onFailure(message: String?) {
                    //выключаем прогресс бар
                    requireActivity().runOnUiThread { progressBar?.hide() }
                    toastService.showToast(R.string.calendar_get_error)
                }

            }, sharedPreferencesService.token, sharedPreferencesService.user.languageId)
        }
    }

    override fun onDetach() {
        super.onDetach()
        //при закрытии фрагмента отменяем все запросы
        requestService?.cancelAllRequests()
        progressBar?.hide()
    }

    private fun Activity.openLink(url: String) {
        try {
            val uri = Uri.parse(url)
            this.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            context?.let { Toast.makeText(it, R.string.calendar_participate_error, Toast.LENGTH_SHORT).show() }
            Log.d("FRAGMENT_REPLACER", "Не удалось открыть ссылку $url")
        }
    }
}