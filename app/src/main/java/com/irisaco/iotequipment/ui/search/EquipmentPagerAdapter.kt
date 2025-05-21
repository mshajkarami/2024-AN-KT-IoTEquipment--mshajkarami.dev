package com.irisaco.iotequipment.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.Equipment
import com.irisaco.iotequipment.R

class EquipmentPagerAdapter(private var equipmentList: List<Equipment>) :
    RecyclerView.Adapter<EquipmentPagerAdapter.EquipmentViewHolder>() {

    class EquipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txt_name)
        val id: TextView = itemView.findViewById(R.id.txt_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment, parent, false)
        return EquipmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val equipment = equipmentList[position]
        holder.title.text = equipment.title ?: "عنوان نامشخص"
        holder.id.text = equipment.type?.name ?: "IR0054534232322225"
    }

    override fun getItemCount(): Int = equipmentList.size

    fun updateData(newList: List<Equipment>) {
        equipmentList = newList
        notifyDataSetChanged()
    }
}