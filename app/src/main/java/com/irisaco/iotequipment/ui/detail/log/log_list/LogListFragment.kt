package com.irisaco.iotequipment.ui.detail.log.log_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.domain.RoleViewModel
import com.irisaco.iotequipment.ui.detail.base_view_model.DetailViewModel
import com.irisaco.iotequipment.ui.detail.base_view_model.DetailViewModelFactory
import com.irisaco.iotequipment.ui.detail.base_view_model.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogListFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: DetailViewModelFactory

    private val roleViewModel: RoleViewModel by viewModels ()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var adapter: EquipmentLogListAdapter
    private lateinit var btnAddLog: ImageButton

    private var equipmentId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        equipmentId = arguments?.getInt("equipmentId") ?: -1

        if (equipmentId == -1) {
            Toast.makeText(context, requireContext().getString(R.string.msg_error_id_invalid), Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private val viewModel: DetailViewModel by viewModels {
        viewModelFactory.create(equipmentId)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_log_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupRecyclerView(view)
        setupObservers()
        setupListeners()
    }


    private fun setupViews(view: View) {
        btnAddLog = view.findViewById(R.id.btn_add_log)
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.logRecyclerView)
        adapter = EquipmentLogListAdapter { record -> openLogDetailDialog(record) }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
//        roleViewModel.canAddOrEditLog.observe(viewLifecycleOwner) { allowed ->
//            btnAddLog.isVisible = allowed
//        }

        viewModel.equipmentRecords.observe(viewLifecycleOwner) { records ->
            adapter.submitList(records)
        }

        viewModel.loadEquipmentRecords()

        sharedViewModel.equipmentId.value = equipmentId
    }

    private fun setupListeners() {
        btnAddLog.setOnClickListener {
            findNavController().navigate(R.id.action_equipmentDetailFragment_to_addLogFragment)
        }
    }

    private fun openLogDetailDialog(records: Records) {
        sharedViewModel.recordEquipmentId.value = records.id
        findNavController().navigate(R.id.action_equipmentDetailFragment_to_logDetailFragment)
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.setToolbarTitle(getString(R.string.toolbar_default_text))
        activity.setDefaultToolbarIcon()

        viewModel.loadEquipmentRecords()
    }
}

