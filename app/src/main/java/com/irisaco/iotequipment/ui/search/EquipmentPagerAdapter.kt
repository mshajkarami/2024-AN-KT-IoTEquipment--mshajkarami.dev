package com.irisaco.iotequipment.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Equipment

class EquipmentPagerAdapter(
    private var equipmentList: List<Equipment>,
    private val onItemClick: (equipmentId: Int) -> Unit
) :
    RecyclerView.Adapter<EquipmentPagerAdapter.EquipmentViewHolder>() {

    inner class EquipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.txt_name)
        val id = itemView.findViewById<TextView>(R.id.txt_id)
        val img = itemView.findViewById<ImageView>(R.id.img)

        fun bind(equipment : Equipment) {
            title.text = equipment.title ?: "عنوان نامشخص"
            id.text = equipment.equipmentUniqueId ?: "IR0054534232322225"
            itemView.setOnClickListener {
                onItemClick(equipment.id ?: 0)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment, parent, false)


        return EquipmentViewHolder(view)

    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val equipment = equipmentList[position]
        holder.bind(equipment)
    }

    override fun getItemCount(): Int = equipmentList.size

    fun updateData(newList: List<Equipment>) {
        equipmentList = newList
        notifyDataSetChanged()
    }
}