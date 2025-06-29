package com.irisaco.iotequipment.ui.detail.equipment_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.detail.DetailViewModel
import com.irisaco.iotequipment.ui.detail.DetailViewModelFactory
import kotlinx.coroutines.launch

class DetailEquipmentFragment : Fragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_equipment_detail, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = DetailViewModelFactory(requireActivity().application, equipmentId)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]


        val titleTextView = view.findViewById<TextView>(R.id.txt_equipment_name)
        val versionTextView = view.findViewById<TextView>(R.id.txt_equipment_version)
        val typeTextView = view.findViewById<TextView>(R.id.txt_equipment_type)
        val descTextView = view.findViewById<TextView>(R.id.txt_equipment_detail)

        val btnAttachment = view.findViewById<Button>(R.id.btn_attachment)


        lifecycleScope.launch {
            val equipment = viewModel.getEquipmentById(equipmentId)
            if (equipment != null) {
                val equipmentName = equipment.title
                titleTextView.text =  getString(R.string.device_name_label, equipmentName)

                val equipmentVersion = equipment.version?.version
                versionTextView.text = getString(R.string.device_version_label, equipmentVersion)


                val equipmentType = equipment.type?.name.toString()
                typeTextView.text = getString(R.string.device_type_label, equipmentType)

                val equipmentDesc = equipment.description
                descTextView.text = equipmentDesc

            } else {
                titleTextView.text = "موردی یافت نشد"
            }
        }

        // open the Attachment Fragment
        btnAttachment.setOnClickListener {
            findNavController().navigate(R.id.action_global_attachmentsFragment)
        }

    }
}