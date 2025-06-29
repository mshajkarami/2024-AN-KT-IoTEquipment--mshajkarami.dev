package com.irisaco.iotequipment.ui.detail.log.log_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.ui.detail.DetailViewModel
import com.irisaco.iotequipment.ui.detail.DetailViewModelFactory
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel

class LogListFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private val vm: EquipmentDetailSharedViewModel by activityViewModels()
    private lateinit var adapter: EquipmentLogListAdapter

    private var equipmentId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        equipmentId = arguments?.getInt("equipmentId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_log_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.equipmentId.value = equipmentId

        val factory = DetailViewModelFactory(requireActivity().application, equipmentId)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        val recyclerView: RecyclerView = view.findViewById(R.id.logRecyclerView)
        adapter = EquipmentLogListAdapter() { record ->
            openLogDetailDialog(record)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.equipmentRecords.observe(viewLifecycleOwner) { recordList ->
            adapter.submitList(recordList)
        }

    }
    private fun openLogDetailDialog(records: Records) {
        vm.recordEquipmentId.value = records.id
        findNavController().navigate(R.id.action_equipmentDetailFragment_to_logDetailFragment)
    }
}
