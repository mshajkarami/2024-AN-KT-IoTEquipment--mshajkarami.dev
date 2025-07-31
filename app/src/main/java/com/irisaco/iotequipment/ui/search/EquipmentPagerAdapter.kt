package com.irisaco.iotequipment.ui.search

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.impl.utils.MatrixExt
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.button.MaterialButton
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Items
import java.util.Locale

class EquipmentPagerAdapter(
    private var equipmentList: List<Items>,
    private val onItemClick: (equipmentId: Int) -> Unit,
    private val onAttachmentClick: (equipmentId: Int) -> Unit
) :
    RecyclerView.Adapter<EquipmentPagerAdapter.EquipmentViewHolder>() {

    inner class EquipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txt_name)
        val id: TextView = itemView.findViewById(R.id.txt_id)
        val img: ImageView = itemView.findViewById<ImageView>(R.id.img)
        val btnAttachment : MaterialButton = itemView.findViewById(R.id.btn_open_attachment)

        fun bind(equipment: Items) {
            val context = itemView.context
            val equipmentName = equipment.title
            val equipmentUniqueId = equipment.equipmentUniqueId
            val imageUrl = equipment.imageUrl

            title.text = context.getString(R.string.equipment_label_name, equipmentName)
            id.text = context.getString(R.string.equipment_label_unique_id, equipmentUniqueId)

            val isRTL = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL
            val iconRes = if (isRTL) R.drawable.ic_left else R.drawable.ic_right
            btnAttachment.setIconResource(iconRes)

            if (imageUrl.isNullOrEmpty()) {
                img.setImageResource(R.drawable.der_img_equipment)
            } else {
                img.load(imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder)
                    error(R.drawable.der_img_equipment)
                }
            }

            itemView.setOnClickListener {
                onItemClick(equipment.id ?: 0)
            }

            btnAttachment.setOnClickListener {
                onAttachmentClick(equipment.id ?: -1)
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Items>) {
        equipmentList = newList
        notifyDataSetChanged()
    }
}