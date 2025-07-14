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
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel
import com.irisaco.iotequipment.utils.FileDownloader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AttachmentsFragment : Fragment() {
    @Inject
    lateinit var assistedFactory: AttachmentsViewModelAssistedFactory
    private lateinit var adapter: AttachmentsAdapter
    private lateinit var recyclerView: RecyclerView
    private val sharedViewModel: EquipmentDetailSharedViewModel by activityViewModels()
    private lateinit var viewModel: AttachmentsViewModel


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
        val combinedLiveData = MediatorLiveData<Pair<Int, Int?>>()

        combinedLiveData.addSource(sharedViewModel.equipmentId) { equipId ->
            val recordId = sharedViewModel.recordEquipmentId.value
            if (equipId != null) combinedLiveData.value = equipId to recordId
        }

        combinedLiveData.addSource(sharedViewModel.recordEquipmentId) { recId ->
            val equipId = sharedViewModel.equipmentId.value
            if (equipId != null) combinedLiveData.value = equipId to recId
        }

        combinedLiveData.observe(viewLifecycleOwner) { (equipId, recId) ->
            setupViewModel(equipId, recId)
        }
    }

    private fun setupViewModel(equipmentId: Int, recordId: Int?) {
        viewModel = ViewModelProvider(
            this,
            AttachmentsViewModelFactory(assistedFactory, equipmentId, recordId)
        )[AttachmentsViewModel::class.java]

        viewModel.documents.observe(viewLifecycleOwner) { documentList ->
            adapter.submitList(documentList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.equipmentId.value = null
        sharedViewModel.recordEquipmentId.value = null
    }
}
