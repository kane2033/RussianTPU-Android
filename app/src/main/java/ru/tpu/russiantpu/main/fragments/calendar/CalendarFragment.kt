package ru.tpu.russiantpu.main.fragments.calendar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import ru.tpu.russiantpu.R
import ru.tpu.russiantpu.dto.EventDTO
import ru.tpu.russiantpu.main.dataAdapters.EventDataAdapter
import ru.tpu.russiantpu.utility.*
import ru.tpu.russiantpu.utility.callbacks.GenericCallback
import ru.tpu.russiantpu.utility.requests.GsonService
import ru.tpu.russiantpu.utility.requests.RequestService
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private lateinit var progressBar: ContentLoadingProgressBar
    private lateinit var calendar: MaterialCalendarView

    private var events: ArrayList<EventDTO>? = null
    private val dates: ArrayList<CalendarDay> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferencesService = SharedPreferencesService(requireActivity())
        //установка языка приложения (интерфейса)
        LocaleService.setLocale(requireActivity(), sharedPreferencesService.languageName)

        progressBar = requireActivity().findViewById(R.id.progress_bar)
        progressBar.show()

        val todayTextView: TextView = view.findViewById(R.id.today_text)

        // инициализация RecyclerView с адаптером
        val eventsRecyclerView: RecyclerView = view.findViewById(R.id.list)
        val eventsAdapter = EventDataAdapter(clickListener = { eventId ->
            // Переносим id в фрагмент деталей о событии, чтобы сделать запрос на событие
            val bundle = Bundle().apply { putString("eventId", eventId) }
            val detailsFragment = CalendarDetailsFragment()
            detailsFragment.arguments = bundle
            requireFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    detailsFragment).addToBackStack(null).commit()
        })
        eventsRecyclerView.adapter = eventsAdapter

        // Настройка календаря
        calendar = view.findViewById(R.id.events_calendar)
        calendar.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                // заголовок
                todayTextView.text = getString(R.string.calendar_today_date,
                        "${date.day}.${date.month}")

                events?.let {
                    val eventsByDate: List<EventDTO> = it.filter { event ->
                        date.date == event.timestamp.toLocalDate()
                    }
                    // отображаем события на выбранный день
                    eventsAdapter.updateList(eventsByDate)
                }
            }
        }

        val requestService = RequestService(sharedPreferencesService, StartActivityService(requireActivity()))
        val gsonService = GsonService()
        val toastService = ToastService(requireContext())

        val token = sharedPreferencesService.token

        requestService.doRequest("user/calendarEvents", object : GenericCallback<String> {
            override fun onResponse(json: String?) {
                // события
                events = gsonService.fromJsonToArrayList(json, EventDTO::class.java)

                //mock
/*                events!!.add(EventDTO("someid", "mock title1", "mock description1", events!![0].timestamp, "ALL"))
                events!!.add(EventDTO("someid", "mock title2", "mock description2", events!![0].timestamp, "ALL"))
                events!!.add(EventDTO("someid", "mock title3", "mock description3", events!![0].timestamp, "ALL"))
                events!!.add(EventDTO("someid", "mock title4", "mock description4", events!![0].timestamp, "ALL"))*/

                // временные отметки календаря materialCalendarView
                events?.let {
                    dates.addAll(it.map { event ->
                        CalendarDay.from(event.timestamp.toLocalDate())
                    })
                }

                requireActivity().runOnUiThread {
                    progressBar.hide()
                    // добавляем точки на дни с событиями
                    val drawable = ContextCompat.getDrawable(requireContext(),
                            R.drawable.circle)
                    drawable?.let { calendar.addDecorator(EventDecorator(drawable, dates)) }

/*                    calendar.addDecorator(EventDecorator(
                            ContextCompat.getColor(this@CalendarActivity, R.color.red_700),
                            dates)
                    )*/
                }
            }

            override fun onError(message: String?) {
                //выключаем прогресс бар
                requireActivity().runOnUiThread { progressBar.hide() }
                toastService.showToast(message)
            }

            override fun onFailure(message: String?) {
                //выключаем прогресс бар
                requireActivity().runOnUiThread { progressBar.hide() }
                toastService.showToast(R.string.calendar_get_error)
            }

        }, token, sharedPreferencesService.languageId)
    }

    fun Date.toLocalDate(): LocalDate =
            Instant.ofEpochMilli(this.time)
                    .atZone(ZoneId.of("Asia/Krasnoyarsk"))
                    .toLocalDate()

}