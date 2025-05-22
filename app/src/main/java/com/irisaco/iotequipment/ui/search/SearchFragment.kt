package com.irisaco.iotequipment.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.utils.SearchViewModelFactory

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val viewPager = view.findViewById<ViewPager2>(R.id.search_view_pager)


        val adapter = EquipmentPagerAdapter(emptyList())
        viewPager.adapter = adapter

        viewModel.devices.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }

        viewModel.loadDevices()

        return view
    }
}

