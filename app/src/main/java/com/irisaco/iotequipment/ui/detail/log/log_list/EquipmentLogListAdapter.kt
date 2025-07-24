package com.irisaco.iotequipment.ui.detail.log.log_list

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Records
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class EquipmentLogListAdapter (
    private val onItemClick: (Records) -> Unit
    ) :
    ListAdapter<Records, EquipmentLogListAdapter.LogViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment_log, parent, false)
        return LogViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_log_name)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_log_description)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(records: Records) {
            val context = itemView.context
            val recordDate = records.creationTime
            val formattedDate = formatIsoDate(recordDate.toString())
            tvDate.text = context.getString(R.string.equipment_label_date, formattedDate)
            val recordDis = records.description
            tvDescription.text = context.getString(R.string.equipment_label_description,recordDis)
            itemView.setOnClickListener {
                onItemClick(records)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Records>() {
        override fun areItemsTheSame(oldItem: Records, newItem: Records): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Records, newItem: Records): Boolean {
            return oldItem == newItem
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatIsoDate(isoDate: String): String {
        return try {
            val cleanedIso = isoDate.replace(Regex("\\.\\d{6}"), "")

            val instant = Instant.parse(cleanedIso)
            val date = Date.from(instant)

            return if (Locale.getDefault().language == "fa") {
                val persianDate = PersianDate(date.time)
                val formatter = PersianDateFormat("Y/m/d")
                formatter.format(persianDate)
            } else {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    .withZone(ZoneId.systemDefault())
                formatter.format(instant)
            }
        } catch (e: Exception) {
            "Invalid date"
        }
    }

}
