package com.irisaco.iotequipment.ui.detail.attachments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.AuthRepository
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import com.irisaco.iotequipment.ui.detail.DetailViewModel
import com.irisaco.iotequipment.ui.detail.DetailViewModelFactory
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel
import com.irisaco.iotequipment.utils.FileDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AttachmentsFragment : Fragment() {

    private lateinit var repository: EquipmentRepository
    private lateinit var adapter: AttachmentsAdapter

    private lateinit var recyclerView: RecyclerView

    private val sharedViewModel: EquipmentDetailSharedViewModel by activityViewModels()
    private var viewModel: AttachmentsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = EquipmentRepository(ApiClient.apiService)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_attachments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.attachmentRecyclerView)
        adapter = AttachmentsAdapter { document ->
            val file = FileDownloader.downloadFile(
                requireContext(),
                document.fileUrl!!,
                document.fileName.toString()
            )
            file?.let {
                FileDownloader.openFile(requireContext(), it)

                val updatedList = adapter.currentList.map { doc ->
                    if (doc.id == document.id) doc.copy()
                    else doc
                }
                withContext(Dispatchers.Main) {
                    adapter.submitList(updatedList)
                }
            }
            file
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        observeViewModelTriggers()
    }

    private fun observeViewModelTriggers() {
        sharedViewModel.equipmentId.observe(viewLifecycleOwner) { equipmentId ->
            if (equipmentId == null) return@observe

            val recordId = sharedViewModel.recordEquipmentId.value
            setupViewModel(equipmentId, recordId)
        }

        sharedViewModel.recordEquipmentId.observe(viewLifecycleOwner) { recordId ->
            val equipmentId = sharedViewModel.equipmentId.value
            if (equipmentId != null) {
                setupViewModel(equipmentId, recordId)
            }
        }
    }

    private fun setupViewModel(equipmentId: Int, recordId: Int?) {
        val factory = AttachmentsViewModelFactory(equipmentId, recordId, repository)
        viewModel = ViewModelProvider(this, factory)[AttachmentsViewModel::class.java]

        viewModel?.documents?.observe(viewLifecycleOwner) { documentList ->
            adapter.submitList(documentList)
        }
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.equipmentId.value = null
        sharedViewModel.recordEquipmentId.value = null
    }
}
