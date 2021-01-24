package ru.tpu.russiantpu.main.dataAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tpu.russiantpu.R
import ru.tpu.russiantpu.dto.EventDTO
import java.text.SimpleDateFormat
import java.util.*

class EventDataAdapter(private val clickListener: (position: String) -> Unit)
    : RecyclerView.Adapter<EventDataAdapter.ViewHolder>() {

    private var events: List<EventDTO> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event: EventDTO = events[position]
        holder.bind(event)
    }

    fun updateList(newEvents: List<EventDTO>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = events.size

    inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.item_event, parent, false)) {
        private val titleView: TextView = itemView.findViewById(R.id.title)
        private val descriptionView: TextView = itemView.findViewById(R.id.description)
        private val dateView: TextView = itemView.findViewById(R.id.date)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    clickListener(events[adapterPosition].id)
                }
            }
        }

        fun bind(event: EventDTO) {
            titleView.text = event.title
            descriptionView.text = event.description
            dateView.text = event.timestamp.toStringDate()

        }

        private fun Date.toStringDate(): String {
            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Krasnoyarsk")
            return simpleDateFormat.format(this)
        }
    }
}