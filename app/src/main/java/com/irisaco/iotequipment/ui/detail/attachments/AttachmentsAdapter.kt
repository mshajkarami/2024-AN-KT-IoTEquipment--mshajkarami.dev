package com.irisaco.iotequipment.ui.detail.attachments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Document
import java.io.File
class AttachmentsAdapter(
    private val onDownloadClicked: (Document, (File?) -> Unit) -> Unit
) : RecyclerView.Adapter<AttachmentsAdapter.AttachmentViewHolder>() {

    private val items = mutableListOf<Document>()
    fun getItems(): List<Document> = items.toList()

    fun submitDocuments(documents: List<Document>) {
        items.clear()
        items.addAll(documents)
        notifyDataSetChanged()
    }

    fun updateItem(document: Document) {
        val index = items.indexOfFirst { it.id == document.id }
        if (index != -1) {
            items[index] = document
            notifyItemChanged(index, "isDownloadingChanged")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment_attachment, parent, false)
        return AttachmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(
        holder: AttachmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            holder.bind(items[position])
        } else {
            holder.partialBind(items[position], payloads)
        }
    }

    override fun getItemCount(): Int = items.size

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    inner class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txt_attachment)
        val icAttachment: ImageView = itemView.findViewById(R.id.ic_attachment)
        val downloadIcon: ImageView = itemView.findViewById(R.id.downloadIcon)
        val progressBar: ProgressBar = itemView.findViewById(R.id.download_progressBar)

        fun bind(document: Document) {
            val context = itemView.context
            txtName.text = context.getString(R.string.equipment_label_attachment, document.fileName)

            when (document.type?.lowercase()) {
                ".pdf" -> icAttachment.setImageResource(R.drawable.ic_pdf)
                ".doc", ".docx" -> icAttachment.setImageResource(R.drawable.ic_word)
                ".xls", ".xlsx" -> icAttachment.setImageResource(R.drawable.ic_exel)
                else -> icAttachment.setImageResource(R.drawable.ic_file)
            }

            val file = File(context.getExternalFilesDir(null), document.fileName)

            val iconColor = if (file.exists())
                R.color.md_theme_primary
            else
                R.color.md_theme_background

            downloadIcon.setColorFilter(ContextCompat.getColor(context, iconColor))

            if (document.isDownloading) {
                showProgress()
            } else {
                hideProgress()
            }

            itemView.setOnClickListener {
                if (document.isDownloading) return@setOnClickListener

                document.isDownloading = true
                updateItem(document)

                onDownloadClicked(document) { downloadedFile ->
                    document.isDownloading = false
                    updateItem(document)

                    if (downloadedFile != null) {
                        downloadIcon.setColorFilter(ContextCompat.getColor(context, R.color.md_theme_primary))
                    } else {
                        Toast.makeText(context, context.getString(R.string.msg_download_fail), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fun partialBind(document: Document, payloads: List<Any>) {
            if (payloads.contains("isDownloadingChanged")) {
                if (document.isDownloading) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }
        }

        private fun showProgress() {
            progressBar.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(300).start()
            }
            itemView.animate().alpha(0.5f).setDuration(300).start()
            downloadIcon.animate().alpha(0f).setDuration(300).start()
        }

        private fun hideProgress() {
            progressBar.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    progressBar.visibility = View.GONE
                }
                .start()
            itemView.animate().alpha(1f).setDuration(300).start()
            downloadIcon.animate().alpha(1f).setDuration(300).start()
        }
    }
}