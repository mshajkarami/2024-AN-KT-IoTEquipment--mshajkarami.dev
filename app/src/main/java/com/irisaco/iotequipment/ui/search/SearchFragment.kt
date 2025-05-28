package com.irisaco.iotequipment.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Equipment

class SearchFragment : Fragment() {

    private lateinit var equipmentList: List<Equipment>
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: EquipmentPagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.search_recyclerView)
        adapter = EquipmentPagerAdapter(emptyList()) {
            equipmentId ->
            bindDetailFragment(equipmentId)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        observeViewModel()

        return view
    }

    private fun observeViewModel() {
        viewModel.equipmentList.observe(viewLifecycleOwner) { equipmentList ->
            adapter.updateData(equipmentList)
            if (equipmentList.isNotEmpty()) {
                equipmentList.forEach { equipment ->
                    Log.i("TAGY", equipment.title.toString())
                }
            } else {
                Log.w("TAGY", "Equipment list is empty or null")
            }
        }
    }
}

private fun SearchFragment.bindDetailFragment(equipmentId: Int) {
    val action = SearchFragmentDirections.actionSearchFragmentToEquipmentDetailFragment(equipmentId)
    findNavController().navigate(action)
}

