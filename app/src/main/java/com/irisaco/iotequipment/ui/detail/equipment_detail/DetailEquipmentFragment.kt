package com.irisaco.iotequipment.ui.detail.equipment_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.databinding.FragmentEquipmentDetailBinding
import com.irisaco.iotequipment.ui.detail.DetailViewModel
import com.irisaco.iotequipment.ui.detail.DetailViewModelFactory
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@AndroidEntryPoint
class DetailEquipmentFragment : Fragment() {
    private lateinit var binding: FragmentEquipmentDetailBinding

    @Inject
    lateinit var viewModelFactory: DetailViewModelFactory
    private val sharedViewModel: EquipmentDetailSharedViewModel by activityViewModels()
    private var equipmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        equipmentId = arguments?.getInt("equipmentId") ?: -1
    }

    private val viewModel: DetailViewModel by viewModels {
        viewModelFactory.create(equipmentId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_equipment_detail, container, false)
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

        binding.btnAttachment.setOnClickListener {
            findNavController().navigate(R .id.action_global_attachmentsFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.setEquipmentId(equipmentId)
    }
}
