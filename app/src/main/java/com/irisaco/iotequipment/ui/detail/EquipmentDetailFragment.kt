package com.irisaco.iotequipment.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.search.SearchFragment
import com.irisaco.iotequipment.ui.search.SearchFragmentDirections
import kotlinx.coroutines.launch

class EquipmentDetailFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel

    private var equipmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = EquipmentDetailFragmentArgs.fromBundle(it)
            equipmentId = args.equipmentId
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
        // Add Top Nav
//        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.btn_top_nav)
//        val menu = bottomNavigationView.menu
//        val homeItem = menu.findItem(R.id.homeFragment)
//        homeItem.isEnabled = false

        // open the Attachment Fragment

        lifecycleScope.launch {
            val equipment = viewModel.getEquipmentById(equipmentId)
            if (equipment != null) {
                titleTextView.text = equipment.title
                versionTextView.text = equipment.version?.version.toString()
                typeTextView.text = equipment.type?.name.toString()
                descTextView.text = equipment.description
            } else {
                titleTextView.text = "موردی یافت نشد"
            }
        }
        btnAttachment.setOnClickListener {
            findNavController().navigate(R.id.action_equipmentDetailFragment_to_attachmentsFragment)
        }

    }
}