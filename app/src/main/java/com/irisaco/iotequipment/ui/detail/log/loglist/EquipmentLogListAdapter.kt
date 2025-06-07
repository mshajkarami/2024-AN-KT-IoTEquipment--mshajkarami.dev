package com.irisaco.iotequipment.ui.detail.log.loglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Record

class EquipmentLogListAdapter :
    ListAdapter<Record, EquipmentLogListAdapter.LogViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_log_date)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_log_description)

        fun bind(record: Record) {
            val context = itemView.context
            val recordDate = record.creationTime
            tvDate.text = context.getString(R.string.device_log_date_label,recordDate)
            val recordDis = record.description
            tvDescription.text = context.getString(R.string.device_detail_label,recordDis)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem == newItem
        }
    }
}
