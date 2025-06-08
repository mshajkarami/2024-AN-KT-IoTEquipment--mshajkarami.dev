package com.irisaco.iotequipment.ui.detail.log.loglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Items
import com.irisaco.iotequipment.data.model.Records

class EquipmentLogListAdapter (
//    private val onItemClick: (logId: Int) -> Unit,
//    private var logList: List<Records>
    ) :
    ListAdapter<Records, EquipmentLogListAdapter.LogViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
//        val logId = logList[position]
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
                Toast.makeText(context,"Log Item Clicked",Toast.LENGTH_LONG).show()
//                onItemClick(logList.size ?: 0)
            }
        }
    }
//    override fun getItemCount(): Int = logList.size


    class DiffCallback : DiffUtil.ItemCallback<Records>() {
        override fun areItemsTheSame(oldItem: Records, newItem: Records): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Records, newItem: Records): Boolean {
            return oldItem == newItem
        }
    }
}
