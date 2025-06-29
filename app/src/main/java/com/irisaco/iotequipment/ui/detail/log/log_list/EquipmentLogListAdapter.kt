package com.irisaco.iotequipment.ui.detail.log.log_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Records

class EquipmentLogListAdapter (
    private val onItemClick: (Records) -> Unit
    ) :
    ListAdapter<Records, EquipmentLogListAdapter.LogViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_log_name)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_log_description)

        fun bind(records: Records) {
            val context = itemView.context
            val recordDate = records.creationTime
            tvDate.text = context.getString(R.string.device_log_date_label,recordDate)
            val recordDis = records.description
            tvDescription.text = context.getString(R.string.device_detail_label,recordDis)
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
}
