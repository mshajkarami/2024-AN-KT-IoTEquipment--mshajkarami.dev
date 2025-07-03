package com.irisaco.iotequipment.ui.detail.equipment_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.databinding.FragmentEquipmentDetailBinding
import com.irisaco.iotequipment.ui.detail.DetailViewModel
import com.irisaco.iotequipment.ui.detail.DetailViewModelFactory
import kotlinx.coroutines.launch

class DetailEquipmentFragment : Fragment() {
    private lateinit var binding: FragmentEquipmentDetailBinding
    private lateinit var viewModel: DetailViewModel
    private var equipmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            equipmentId = arguments?.getInt("equipmentId") ?: -1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = DetailViewModelFactory(requireActivity().application, equipmentId)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_equipment_detail, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            val equipment = viewModel.getEquipmentById(equipmentId)
            if (equipment != null) {
                viewModel.title.value = equipment.title
                viewModel.version.value = equipment.version?.version
                viewModel.type.value = equipment.type?.name ?: ""
                viewModel.description.value = equipment.description

            } else {
                viewModel.title.value = "موردی یافت نشد"
            }
        }

        // open the Attachment Fragment
        binding.btnAttachment.setOnClickListener {
            findNavController().navigate(R.id.action_global_attachmentsFragment)
        }

    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setToolbarTitle(getString(R.string.toolbar_base_text))
    }
}