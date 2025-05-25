package com.irisaco.iotequipment.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.repository.EquipmentRepository


class SearchFragment : Fragment() {

    private lateinit var repository: EquipmentRepository
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val viewPager = view.findViewById<ViewPager2>(R.id.search_view_pager)

        viewModel.equipmentList.observe(viewLifecycleOwner) { equipmentList ->
            if (equipmentList.isNotEmpty()) {
                equipmentList.forEach { equipment ->
                    Log.i("TAGY", equipment.title.toString())
                }
            } else {
                Log.w("TAGY", "Equipment list is empty or null")
            }
        }

        return view
    }
}

