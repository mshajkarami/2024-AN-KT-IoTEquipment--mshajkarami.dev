package com.irisaco.iotequipment.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.ui.scanner.QrScannerActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.base.BaseFragment

class HomeFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchBox = view.findViewById<TextInputLayout>(R.id.searchBox)
        val searchBar = view.findViewById<TextInputEditText>(R.id.search_bar)
        val btnScan: FloatingActionButton = view.findViewById(R.id.btn_scan)

        searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchBar.clearFocus()
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }
        }

        searchBox.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        btnScan.setOnClickListener {
            startActivity(Intent(requireContext(), QrScannerActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        val scannedId = activity.equipmentIdUnique
        if (!scannedId.isNullOrEmpty()) {
            activity.equipmentIdUnique = null
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .build()

            val action = HomeFragmentDirections
                .actionHomeFragmentToSearchFragment(scannedId)
            findNavController().navigate(action, navOptions)
        }
    }
}
