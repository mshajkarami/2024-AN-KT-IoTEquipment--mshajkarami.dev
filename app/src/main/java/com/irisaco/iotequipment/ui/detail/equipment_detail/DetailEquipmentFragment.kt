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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.airbnb.lottie.LottieAnimationView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.databinding.FragmentEquipmentDetailBinding
import com.irisaco.iotequipment.ui.detail.base_view_model.DetailViewModel
import com.irisaco.iotequipment.ui.detail.base_view_model.DetailViewModelFactory
import com.irisaco.iotequipment.ui.detail.base_view_model.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue
@AndroidEntryPoint
class DetailEquipmentFragment : Fragment() {
    private lateinit var binding: FragmentEquipmentDetailBinding

    private lateinit var lomAdapter: EquipmentLomAdapter

    @Inject
    lateinit var viewModelFactory: DetailViewModelFactory
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var equipmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        equipmentId = sharedViewModel.equipmentId.value ?: -1
        sharedViewModel.recordId.value = null
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
        val progressbar = view.findViewById<LottieAnimationView>(R.id.progressBar)
        val contentLayout = view.findViewById<View>(R.id.content_layout)

        binding.btnAttachment.isEnabled = false
        binding.btnAttachment.visibility = View.INVISIBLE

        lifecycleScope.launch {
            val equipment = viewModel.getEquipmentById(equipmentId)
            if (equipment != null) {
                viewModel.title.value = equipment.title
                viewModel.version.value = equipment.version?.version
                viewModel.type.value = equipment.equipmentType?.name ?: ""
                viewModel.description.value = equipment.description

                if (!equipment.equipmentLOM.isNullOrEmpty()) {
                    lomAdapter = EquipmentLomAdapter(equipment.equipmentLOM)

                    binding.equipmentLomRecyclerView.apply {
                        layoutManager = GridLayoutManager(
                            requireContext(),
                            2,
                            GridLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = lomAdapter
                        visibility = View.VISIBLE
                    }

                } else {
                    binding.equipmentLomRecyclerView.visibility = View.GONE
                }



                viewModel.img.observe(viewLifecycleOwner) { imageUrl ->
                    if (imageUrl.isNullOrEmpty()) {
                        binding.imgEquipment.setImageResource(R.drawable.der_img_equipment)
                    } else {
                        binding.imgEquipment.load(imageUrl) {
                            crossfade(true)
                            placeholder(R.drawable.placeholder)
                            error(R.drawable.der_img_equipment)
                        }
                    }
                }


                binding.btnAttachment.isEnabled = true
                binding.btnAttachment.visibility = View.VISIBLE

                progressbar.visibility = View.GONE
                contentLayout.visibility = View.VISIBLE
            } else {
                viewModel.title.value = requireContext().getString(R.string.equipment_not_found)
                binding.btnAttachment.visibility = View.GONE
                progressbar.visibility = View.GONE
                contentLayout.visibility = View.VISIBLE
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
