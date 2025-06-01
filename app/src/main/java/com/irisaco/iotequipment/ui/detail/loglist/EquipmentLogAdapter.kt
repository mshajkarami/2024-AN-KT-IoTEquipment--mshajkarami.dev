package com.irisaco.iotequipment.ui.detail.loglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Equipment

class EquipmentLogAdapter(
    private var equipmentList: List<Equipment>,
    private val onItemClick: (equipmentId: Int) -> Unit
) :
    RecyclerView.Adapter<EquipmentLogAdapter.EquipmentViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EquipmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log,parent,false)
        return EquipmentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: EquipmentViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = equipmentList.size

    inner class EquipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.txt_log_title)
        val detail = itemView.findViewById<TextView>(R.id.txt_log_detail)
        val btnEdit = itemView.findViewById<ImageButton>(R.id.btn_log_edit)
        val btnTrash = itemView.findViewById<ImageButton>(R.id.btn_log_trash)

        fun bind(equipment: Equipment) {
            TODO("Not Records Model Type From The Server")
            title.text = "عنوان نامشخص"
            detail.text = equipment.records.size.toString()
            itemView.setOnClickListener {
                onItemClick(equipment.id ?: 0)
            }
        }
    }

}