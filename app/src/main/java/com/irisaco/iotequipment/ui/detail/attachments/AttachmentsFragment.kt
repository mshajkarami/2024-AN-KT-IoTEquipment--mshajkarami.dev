package com.irisaco.iotequipment.ui.detail.attachments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.base.BaseFragment
import com.irisaco.iotequipment.ui.detail.base_view_model.SharedViewModel
import com.irisaco.iotequipment.utils.FileDownloader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@AndroidEntryPoint
class AttachmentsFragment : BaseFragment() {
    @Inject
    lateinit var fileDownloader: FileDownloader
    @Inject
    lateinit var assistedFactory: AttachmentsViewModelAssistedFactory
    private lateinit var adapter: AttachmentsAdapter
    private lateinit var recyclerView: RecyclerView
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: AttachmentsViewModel

    override fun getLayoutId(): Int = R.layout.fragment_attachments

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
        observeDownloadAllTrigger()
        observeRefreshAllTrigger()
        observeShareAllTrigger()
        swipeRefreshLayout.setOnRefreshListener {
            refreshDocuments()
        }
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
    private fun observeDownloadAllTrigger() {
        sharedViewModel.downloadAllTrigger.observe(viewLifecycleOwner) {
            downloadAllAttachments()
        }
    }
    private fun observeShareAllTrigger(){
        sharedViewModel.triggerShareAll.observe(viewLifecycleOwner) {
            shareAllAttachments()
        }
    }

    private fun observeRefreshAllTrigger(){
        sharedViewModel.triggerRefresh.observe(viewLifecycleOwner) {
            refreshDocuments()
        }
    }
    private fun downloadAllAttachments() {
        val documents = adapter.getItems()

        if (documents.isEmpty()) return

        lifecycleScope.launch {
            var successCount = 0
            var failCount = 0

            for (document in documents) {
                val file = fileDownloader.downloadFile(
                    requireContext(),
                    document.fileUrl!!,
                    document.fileName.toString()
                )
                if (file != null) successCount++ else failCount++
            }

            val message = when {
                failCount == 0 -> getString(R.string.download_all_success)
                successCount == 0 -> getString(R.string.download_all_failed)
                else -> getString(R.string.download_summary, successCount, failCount)
            }
            view?.let {
                Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun shareAllAttachments() {
        val documents = adapter.getItems()

        if (documents.isEmpty()) return

        lifecycleScope.launch {
            val uris = arrayListOf<Uri>()

            for (document in documents) {
                val file = fileDownloader.downloadFile(
                    requireContext(),
                    document.fileUrl ?: continue,
                    document.fileName.toString()
                )

                if (file != null) {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context?.packageName}.provider",
                        file
                    )

                    uris.add(uri)
                }
            }

            if (uris.isEmpty()) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.share_failed_no_files),
                    Snackbar.LENGTH_SHORT
                ).show()
                return@launch
            }

            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "*/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)))
        }
    }

    private fun setupViewModel(equipmentId: Int, recordId: Int?) {
        viewModel = ViewModelProvider(
            this,
            AttachmentsViewModelFactory(assistedFactory, equipmentId, recordId)
        )[AttachmentsViewModel::class.java]

        viewModel.documents.observe(viewLifecycleOwner) { documentList ->
            adapter.submitDocuments(documentList)
        }
    }
    private fun refreshDocuments() {
        viewModel.refreshDocuments()
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.equipmentId.value = null
        sharedViewModel.recordEquipmentId.value = null
    }
}