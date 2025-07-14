package com.irisaco.iotequipment.ui.detail.log.log_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.repository.AuthRepository
import com.irisaco.iotequipment.domain.RoleViewModel
import com.irisaco.iotequipment.domain.RoleViewModelFactory
import com.irisaco.iotequipment.ui.detail.DetailViewModel
import com.irisaco.iotequipment.ui.detail.DetailViewModelAssistedFactory
import com.irisaco.iotequipment.ui.detail.DetailViewModelFactory
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: DetailViewModelFactory

    private lateinit var roleViewModel: RoleViewModel
    private val sharedVm: EquipmentDetailSharedViewModel by activityViewModels()

    private lateinit var adapter: EquipmentLogListAdapter
    private lateinit var btnAddLog: ImageButton

    private var equipmentId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        equipmentId = arguments?.getInt("equipmentId") ?: -1

        if (equipmentId == -1) {
            Toast.makeText(context, "شناسه تجهیز نامعتبر است.", Toast.LENGTH_SHORT).show()
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
        setupViewModels()
        setupViews(view)
        setupRecyclerView(view)
        setupObservers()
        setupListeners()
    }

    private fun setupViewModels() {
        val context = requireContext()
        val authRepository = AuthRepository(context)
        val roleFactory = RoleViewModelFactory(authRepository)
        roleViewModel = ViewModelProvider(this, roleFactory)[RoleViewModel::class.java]
        sharedVm.equipmentId.value = equipmentId
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
        roleViewModel.canAddOrEditLog.observe(viewLifecycleOwner) { allowed ->
            btnAddLog.isVisible = allowed
        }

        viewModel.equipmentRecords.observe(viewLifecycleOwner) { records ->
            adapter.submitList(records)
        }

        viewModel.loadEquipmentRecords()
    }

    private fun setupListeners() {
        btnAddLog.setOnClickListener {
            findNavController().navigate(R.id.action_equipmentDetailFragment_to_addLogFragment)
        }
    }

    private fun openLogDetailDialog(records: Records) {
        sharedVm.recordEquipmentId.value = records.id
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

