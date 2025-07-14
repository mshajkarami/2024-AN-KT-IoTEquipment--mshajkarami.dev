package com.irisaco.iotequipment.ui.detail.attachments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel
import com.irisaco.iotequipment.utils.FileDownloader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AttachmentsFragment : Fragment() {

    @Inject lateinit var fileDownloader: FileDownloader
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
        adapter = AttachmentsAdapter { document, callback ->
            lifecycleScope.launch {
                val file = fileDownloader.downloadFile(
                    requireContext(),
                    document.fileUrl!!,
                    document.fileName.toString()
                )
                if (file != null) {
                    fileDownloader.openFile(requireContext(), file)
                }
                callback(file)
            }
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
