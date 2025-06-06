package com.irisaco.iotequipment.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.irisaco.iotequipment.ui.scanner.QrScannerActivity
import com.irisaco.iotequipment.R

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_home, container, false)
        val searchBox = view.findViewById<TextInputLayout>(R.id.searchBox)
        val searchBar = view.findViewById<TextInputEditText>(R.id.search_bar)
        val btnScan: ImageButton = view.findViewById(R.id.btn_scan)

        searchBox.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
        btnScan.setOnClickListener {
            startActivity(Intent(requireContext(), QrScannerActivity::class.java))
        }
        searchBar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
        return view
    }

}