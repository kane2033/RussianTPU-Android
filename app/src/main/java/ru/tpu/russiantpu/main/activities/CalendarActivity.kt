package ru.tpu.russiantpu.main.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.tpu.russiantpu.R
import ru.tpu.russiantpu.main.fragments.calendar.CalendarFragment
import ru.tpu.russiantpu.utility.LocaleService
import ru.tpu.russiantpu.utility.SharedPreferencesService


class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferencesService = SharedPreferencesService(this)

        //получение пользователя из памяти
        val user = sharedPreferencesService.user
        //установка языка приложения (интерфейса)
        LocaleService.setLocale(this, user.languageName)

        setContentView(R.layout.activity_calendar)

        //нажатие кнопки назад
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Не даем активити добавить еще один фрагмент, если восстанавливаем состояние
        // (пр.: смена ориентации)

        // Не даем активити добавить еще один фрагмент, если восстанавливаем состояние
        // (пр.: смена ориентации)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    CalendarFragment()).commit()
        }
    }
}