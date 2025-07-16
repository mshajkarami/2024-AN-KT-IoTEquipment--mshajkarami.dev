package com.irisaco.iotequipment.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.detail.base_view_model.SharedViewModel

class DetailFragment : Fragment() {

    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout: TabLayout
    private var equipmentId: Int = -1

    val vm by activityViewModels<SharedViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = DetailFragmentArgs.fromBundle(it)
            equipmentId = args.equipmentId
            vm.equipmentId.value = equipmentId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tab_state_description)
        val args = DetailFragmentArgs.fromBundle(requireArguments())
        val adapter = DetailPagerAdapter (this,args.equipmentId)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2

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