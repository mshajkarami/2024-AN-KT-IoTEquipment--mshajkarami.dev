package com.irisaco.iotequipment.ui.detail.attachments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Document

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
        private val baseAttachment: ConstraintLayout = itemView.findViewById(R.id.base_attachment)

        fun bind(document: Document) {
            Log.d("AttachmentsAdapter", "Document full object: $document")
            val context = itemView.context
            val attachmentName = document.fileName
            val attachmentType = document.type.toString()
            txtName.text = context.getString(R.string.label_device_attachment, attachmentName)

            if (attachmentType == ".pdf") {
                icAttachment.setImageResource(R.drawable.ic_pdf)
            } else if (attachmentType == ".docx" || attachmentType == ".doc") {
                icAttachment.setImageResource(R.drawable.ic_word)
            } else if (attachmentType == ".xls" || attachmentType == ".xlsx") {
                icAttachment.setImageResource(R.drawable.ic_exel)
            } else {
                icAttachment.setImageResource(R.drawable.ic_file)
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