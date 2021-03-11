package ru.tpu.russiantpu.main.fragments.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.tpu.russiantpu.R
import ru.tpu.russiantpu.utility.PersonalInfoContent

/**
 * Фрагмент хранит кнопки,
 * ссылающие на соответствующие разделы портала ТПУ.
 */
class TpuPortalFragment : Fragment(R.layout.fragment_tpu_portal), PersonalInfoContent {

    private var academicPlanUrl: String? = null
    private var scheduleUrl: String? = null

    override fun onAcademicPlanUrlReceived(url: String) {
        academicPlanUrl = url
    }

    override fun onScheduleUrlReceived(url: String) {
        scheduleUrl = url
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.button_academic_plan).setOnClickListener { academicPlanUrl?.let { goToLink(it) } }
        view.findViewById<View>(R.id.button_schedule).setOnClickListener { scheduleUrl?.let { goToLink(it) } }
        view.findViewById<View>(R.id.button_personal_card).setOnClickListener { goToPortalLink("private") }
        view.findViewById<View>(R.id.button_grades).setOnClickListener { goToPortalLink("study") }
        view.findViewById<View>(R.id.button_scholarship).setOnClickListener { goToPortalLink("stipend") }
        view.findViewById<View>(R.id.button_payment).setOnClickListener { goToPortalLink("oplata") }
        view.findViewById<View>(R.id.button_my_payments).setOnClickListener { goToPortalLink("oplata/mypayment") }
        view.findViewById<View>(R.id.button_mail).setOnClickListener { goToLink("https://mail.tpu.ru/") }
    }

    // Переход на одну из страниц портала ТПУ
    private fun goToPortalLink(url: String) {
        val fullUrl = "https://portal.tpu.ru/desktop/student/$url"
        try {
            activity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Переход на любой url
    private fun goToLink(fullUrl: String) {
        try {
            activity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}