package com.irisaco.iotequipment.ui.detail.attachments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.detail.DetailViewModel
import com.irisaco.iotequipment.ui.detail.DetailViewModelFactory
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel

class AttachmentsFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var adapter: AttachmentsAdapter

    private var equipmentId: Int = 1

    private val vm: EquipmentDetailSharedViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_attachments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setToolbarTitle(getString(R.string.toolbar_attachment_text))
        val recyclerView: RecyclerView = view.findViewById(R.id.attachmentRecyclerView)
        adapter = AttachmentsAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        vm.equipmentId.value?.let{ id ->
            equipmentId = id


            val factory = DetailViewModelFactory(requireActivity().application, equipmentId)
            viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

            viewModel.equipmentDocument.observe(viewLifecycleOwner) { documentList ->
                adapter.submitList(documentList)
            }
        } ?: run {
            Log.e("AttachmentsFragment", "equipmentId was null")
        }

    }

}