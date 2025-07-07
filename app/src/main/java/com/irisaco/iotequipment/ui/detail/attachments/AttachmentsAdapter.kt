package com.irisaco.iotequipment.ui.detail.attachments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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

class AttachmentsAdapter :
    ListAdapter<Document, AttachmentsAdapter.AttachmentViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment_attachment, parent, false)
        return AttachmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView = itemView.findViewById(R.id.txt_attachment)
        private val icAttachment: ImageView = itemView.findViewById(R.id.ic_attachment)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.download_progressBar)

        fun bind(document: Document) {
            Log.d("AttachmentsAdapter", "Document full object: $document")
            val context = itemView.context
            val attachmentName = document.fileName
            val attachmentType = document.type.toString()
            txtName.text = context.getString(R.string.label_device_attachment, attachmentName)

            when (attachmentType) {
                ".pdf" -> icAttachment.setImageResource(R.drawable.ic_pdf)
                ".docx", ".doc" -> icAttachment.setImageResource(R.drawable.ic_word)
                ".xls", ".xlsx" -> icAttachment.setImageResource(R.drawable.ic_exel)
                else -> icAttachment.setImageResource(R.drawable.ic_file)
            }
            itemView.setOnClickListener {
                itemView.alpha = 0.5f
                progressBar.visibility = View.VISIBLE

                val fileUrl = document.fileUrl ?: return@setOnClickListener
                val fileName = document.fileName ?: "attachment"

                CoroutineScope(Dispatchers.IO).launch {
                    val file = FileDownloader.downloadFile(context, fileUrl, fileName)

                    withContext(Dispatchers.Main) {
                        itemView.alpha = 1.0f
                        progressBar.visibility = View.GONE

                        if (file != null) {
                            Toast.makeText(context, "فایل دانلود شد ✅", Toast.LENGTH_SHORT).show()
                            FileDownloader.openFile(context, file)
                        } else {
                            Toast.makeText(context, "دانلود ناموفق بود ❌", Toast.LENGTH_SHORT).show()
                        }
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