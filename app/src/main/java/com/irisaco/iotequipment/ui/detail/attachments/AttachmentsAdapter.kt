package com.irisaco.iotequipment.ui.detail.attachments

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Document
import com.irisaco.iotequipment.utils.FileDownloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
class AttachmentsAdapter(
    private val onDownloadClicked: (Document, (File?) -> Unit) -> Unit
) : ListAdapter<Document, AttachmentsAdapter.AttachmentViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment_attachment, parent, false)
        return AttachmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateItem(document: Document) {
        val index = currentList.indexOfFirst { it.id == document.id }
        if (index != -1) {
            val newList = currentList.toMutableList()
            newList[index] = document
            submitList(newList)
        }
    }

    inner class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView = itemView.findViewById(R.id.txt_attachment)
        private val icAttachment: ImageView = itemView.findViewById(R.id.ic_attachment)
        private val downloadIcon: ImageView = itemView.findViewById(R.id.downloadIcon)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.download_progressBar)

        @SuppressLint("UseKtx")
        fun bind(document: Document) {
            val context = itemView.context
            txtName.text = context.getString(R.string.label_device_attachment, document.fileName)

            when (document.type?.lowercase()) {
                ".pdf" -> icAttachment.setImageResource(R.drawable.ic_pdf)
                ".doc", ".docx" -> icAttachment.setImageResource(R.drawable.ic_word)
                ".xls", ".xlsx" -> icAttachment.setImageResource(R.drawable.ic_exel)
                else -> icAttachment.setImageResource(R.drawable.ic_file)
            }

            val file = File(context.getExternalFilesDir(null), document.fileName)

            if (file.exists()) {
                downloadIcon.setColorFilter(ContextCompat.getColor(context, R.color.icon_downloaded))
                progressBar.visibility = View.GONE
                itemView.alpha = 1f
            } else {
                downloadIcon.setColorFilter(ContextCompat.getColor(context, R.color.md_theme_background))
                progressBar.visibility = View.GONE
                itemView.alpha = 1f
            }

            itemView.setOnClickListener {
                if (progressBar.visibility == View.VISIBLE) return@setOnClickListener

                progressBar.visibility = View.VISIBLE
                itemView.alpha = 0.5f

                onDownloadClicked(document) { downloadedFile ->
                    progressBar.visibility = View.GONE
                    itemView.alpha = 1.0f

                    if (downloadedFile != null) {
                        updateItem(document)
                    } else {
                        Toast.makeText(context, "دانلود ناموفق بود ❌", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Document>() {
        override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem == newItem
        }
    }
}

