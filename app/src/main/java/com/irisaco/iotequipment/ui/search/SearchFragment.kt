package com.irisaco.iotequipment.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.irisaco.iotequipment.di.MyApplication
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Equipment
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.AuthRepository
import com.irisaco.iotequipment.ui.detail.EquipmentDetailFragmentArgs
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var equipmentList: List<Equipment>
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: EquipmentPagerAdapter
    private lateinit var searchBar: TextInputEditText

    private val args: SearchFragmentArgs by navArgs()

    private var equipmentIdUnique: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        equipmentIdUnique = args.equipmentIdUnique
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.search_recyclerView)

        searchBar = view.findViewById(R.id.search_bar)

        adapter = EquipmentPagerAdapter(emptyList()) { equipmentId ->
            bindDetailFragment(equipmentId)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        observeViewModel()

        val authDao = MyApplication.database.authTokenDao()
        val authRepository = AuthRepository(ApiClient.authService, authDao)

        lifecycleScope.launch {
            val getToken = authRepository.getSavedToken()
            Log.d("Auth", "AccessToken : ${getToken!!.accessToken}")
            Log.d("Auth", "RefreshToken : ${getToken!!.refreshToken}")
        }

        searchBar.setText(equipmentIdUnique)


        return view
    }

    private fun observeViewModel() {
        viewModel.equipmentList.observe(viewLifecycleOwner) { equipmentList ->
            adapter.updateData(equipmentList)
            if (equipmentList.isNotEmpty()) {
                equipmentList.forEach { equipment ->
                    Log.i("TAGY", equipment.title.toString())
                }
            } else {
                Log.w("TAGY", "Equipment list is empty or null")
            }
        }
    }
}

private fun SearchFragment.bindDetailFragment(equipmentId: Int) {
    val action = SearchFragmentDirections.actionSearchFragmentToEquipmentDetailFragment(equipmentId)
    findNavController().navigate(action)
}

