package ru.tpu.russiantpu.main.dataAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tpu.russiantpu.R
import ru.tpu.russiantpu.dto.DocumentDTO

//DataAdapter для списка превью статей
class DocumentsDataAdapter(private val clickListener: (document: DocumentDTO) -> Unit)
    : RecyclerView.Adapter<DocumentsDataAdapter.ViewHolder>() {

    private var documents: List<DocumentDTO> = emptyList() // текущие отображаемые
    private var newDocuments: List<DocumentDTO> = emptyList() // новые, не просмотренные документы
    private var seenDocuments: List<DocumentDTO> = emptyList() // уже скачанные, просмотренные доки

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsDataAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: DocumentsDataAdapter.ViewHolder, position: Int) =
            holder.bind(documents[position])

    override fun getItemCount() = documents.size

    private fun initRecycler(list: List<DocumentDTO>) {
        if (documents != list) {
            documents = list
            notifyDataSetChanged()
        }
    }

    /*
    * При обновлении recycler делим на два списка: непросмотренные и просмотренные документы
    * Если lastUseDate != null, значит документ был скачан хотя бы один раз, следовательно,
    * просмотрен.
    * */
    fun updateList(newDocuments: List<DocumentDTO>) {
        val (seenList, newList) = newDocuments.partition { it.lastUseDate != null }
        this.newDocuments = newList
        this.seenDocuments = seenList
        initRecycler(this.newDocuments)
    }

    fun showNewDocuments() {
        initRecycler(newDocuments)
    }

    fun showSeenDocuments() {
        initRecycler(seenDocuments)
    }

    inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.item_document, parent, false)) {
        private val nameView: TextView = itemView.findViewById(R.id.doc_name)
        private val loadDateView: TextView = itemView.findViewById(R.id.doc_date)
        private val fileNameView: TextView = itemView.findViewById(R.id.doc_file_name)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    clickListener(documents[adapterPosition])
                }
            }
        }

        fun bind(document: DocumentDTO) {
            val name = "\"" + document.name + "\"" //выводим название в кавычках
            nameView.text = name
            loadDateView.text = document.loadDate
            fileNameView.text = document.fileName
        }
    }
}