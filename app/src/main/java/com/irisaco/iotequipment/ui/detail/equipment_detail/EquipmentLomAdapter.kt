package com.irisaco.iotequipment.ui.detail.equipment_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.EquipmentLOM
import com.irisaco.iotequipment.databinding.ItemEquipmentLomBinding

class EquipmentLomAdapter(
    private val items: List<EquipmentLOM>
) : RecyclerView.Adapter<EquipmentLomAdapter.LOMViewHolder>(){

    inner class LOMViewHolder(val binding: ItemEquipmentLomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EquipmentLOM) {
            val context = itemView.context

            binding.txtLomName.text = context.getString(R.string.equipment_label_lom_part_name,item.name)
            binding.txtLomNumber.text = context.getString(R.string.equipment_label_lom_part_quantity,item.quantity.toString())
            binding.txtLomSupplier.text = context.getString(R.string.equipment_label_lom_part_supplier,item.supplier)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LOMViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEquipmentLomBinding.inflate(inflater, parent, false)
        return LOMViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: LOMViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

}