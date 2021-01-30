package ru.tpu.russiantpu.main.dataAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tpu.russiantpu.R
import ru.tpu.russiantpu.dto.NotificationDTO

//DataAdapter для списка превью статей
class NotificationsDataAdapter(private val clickListener: (notification: NotificationDTO) -> Unit = {})
    : RecyclerView.Adapter<NotificationsDataAdapter.ViewHolder>() {

    private var notifications: List<NotificationDTO> = emptyList() // текущие отображаемые

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsDataAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: NotificationsDataAdapter.ViewHolder, position: Int) =
            holder.bind(notifications[position])

    override fun getItemCount() = notifications.size

    /*
    * При обновлении recycler делим на два списка: непросмотренные и просмотренные документы
    * Если lastUseDate != null, значит документ был скачан хотя бы один раз, следовательно,
    * просмотрен.
    * */
    fun updateList(newNotifications: List<NotificationDTO>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.item_notification, parent, false)) {
        private val titleView: TextView = itemView.findViewById(R.id.notif_title)
        private val messageView: TextView = itemView.findViewById(R.id.notif_message)
        private val dateView: TextView = itemView.findViewById(R.id.notif_date)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    clickListener(notifications[adapterPosition])
                }
            }
        }

        fun bind(notification: NotificationDTO) {
            titleView.text = notification.title
            messageView.text = notification.message
            dateView.text = notification.sendDate
        }
    }
}