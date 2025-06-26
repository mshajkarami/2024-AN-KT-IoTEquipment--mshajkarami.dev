package com.irisaco.iotequipment.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.irisaco.iotequipment.ui.detail.equipment_detail.DetailEquipmentFragment
import com.irisaco.iotequipment.ui.detail.log.log_list.LogListFragment


class DetailPagerAdapter(
    fragment: Fragment,
    private val equipmentId: Int
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2


    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> DetailEquipmentFragment()
            1 -> LogListFragment()
            else -> throw IllegalArgumentException("Invalid tab index")
        }
        fragment.arguments = Bundle().apply {
            putInt("equipmentId", equipmentId)
        }
        return fragment
    }
}
