package com.irisaco.iotequipment.ui.detail

import android.R.attr.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.irisaco.iotequipment.ui.detail.descriptin.DescriptionFragment
import com.irisaco.iotequipment.ui.detail.loglist.LogListFragment


class DeviceDetailPagerAdapter(
    fragment: Fragment,
    private val equipmentId: Int
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> DescriptionFragment()
            1 -> LogListFragment()
            else -> throw IllegalArgumentException("Invalid tab index")
        }
        fragment.arguments = Bundle().apply {
            putInt("equipmentId", equipmentId)
        }
        return fragment
    }
}
