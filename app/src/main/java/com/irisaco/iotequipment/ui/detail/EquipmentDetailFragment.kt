package com.irisaco.iotequipment.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.irisaco.iotequipment.R

class EquipmentDetailFragment : Fragment() {

    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout: TabLayout
    private var equipmentId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = EquipmentDetailFragmentArgs.fromBundle(it)
            equipmentId = args.equipmentId        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_equipment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tab_state_description)
        val args = EquipmentDetailFragmentArgs.fromBundle(requireArguments())
        val adapter = DeviceDetailPagerAdapter (this,args.equipmentId)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager){
            tab,position -> tab.text = when (position) {
                0 -> "توضیحات"
                1 -> "لاگ ها"
                else -> ""
            }
        }.attach()
        super.onViewCreated(view, savedInstanceState)
    }
}