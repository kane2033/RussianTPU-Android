package ru.tpu.russiantpu.utility.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ru.tpu.russiantpu.main.activities.ProfileActivity

// Объект, ответственный за открытие
// соответствующего активити в зависимости
// от сообщения в уведомлении
object NotificationResolver {

    const val APP_LINK_KEY = "APP_LINK"
    const val DOCUMENT = "DOCUMENT"
    const val NOTIFICATION = "NOTIFICATION"

    fun getPendingIntent(link: String?, context: Context): PendingIntent? {
        val intent = when (link) {
            DOCUMENT -> {
                Intent(context, ProfileActivity::class.java).apply {
                    putExtra(APP_LINK_KEY, DOCUMENT)
                }
            }
            NOTIFICATION -> {
                Intent(context, ProfileActivity::class.java).apply {
                    putExtra(APP_LINK_KEY, NOTIFICATION)
                }
            }
            else -> return null
        }

/*        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntentWithParentStack(intent)
        // Get the PendingIntent containing the entire back stack
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)*/
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
    }
}