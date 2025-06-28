package com.irisaco.iotequipment.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

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
        setupSearchBarListener()

        if (!equipmentIdUnique.isNullOrBlank()) {
            searchBar.setText(equipmentIdUnique)
            viewModel.onSearch(equipmentIdUnique!!, true)
        } else {
            viewModel.startAutoRefresh(3000L)
        }


        // Get AccessToken Data
        val authDao = MyApplication.database.authTokenDao()
        val authRepository = AuthRepository(ApiClient.authService, authDao)
        lifecycleScope.launch {
            val getToken = authRepository.getSavedToken()
            Log.d("Auth", "AccessToken : ${getToken!!.accessToken}")
            Log.d("Auth", "RefreshToken : ${getToken!!.refreshToken}")
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val searchBar = view.findViewById<TextInputEditText>(R.id.search_bar)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        searchBar.requestFocus()
        searchBar.postDelayed({
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

    private fun observeViewModel() {
        viewModel.equipmentList.observe(viewLifecycleOwner) { equipmentList ->
            adapter.updateData(equipmentList)
            if (equipmentList.isNotEmpty()) {
                equipmentList.forEach { equipment ->
                    Log.i("TAGY", equipment.title.toString())
                }
            } else {
                Log.w("TAGY", "No equipment found for this query")
            }
        }
    }

    private fun setupSearchBarListener() {
        var searchJob: Job? = null

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    val query = s.toString().trim()
                    if (query.isEmpty()) {
                        viewModel.clearSearchAndResumeAutoRefresh()
                    } else {
                        viewModel.onSearch(query, true)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchBar.text.toString().trim()
                if (query.isEmpty()) {
                    viewModel.clearSearchAndResumeAutoRefresh()
                } else {
                    viewModel.onSearch(query, true)
                }
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchBar.windowToken, 0)
                true
            } else {
                false
            }
        }
    }
    private fun bindDetailFragment(equipmentId: Int) {
        val action =
            SearchFragmentDirections.actionSearchFragmentToEquipmentDetailFragment(equipmentId)
        findNavController().navigate(action)
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopAutoRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchBar.setOnEditorActionListener(null)
        searchBar.addTextChangedListener(null)
    }

}



