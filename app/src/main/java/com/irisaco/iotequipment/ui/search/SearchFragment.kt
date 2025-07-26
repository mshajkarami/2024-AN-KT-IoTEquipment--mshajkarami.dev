package com.irisaco.iotequipment.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputEditText
import com.irisaco.iotequipment.ui.base.BaseFragment
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.components.SkeletonAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment() {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: EquipmentPagerAdapter
    private lateinit var searchBar: TextInputEditText
    private val args: SearchFragmentArgs by navArgs()
    private val skeletonAdapter by lazy { SkeletonAdapter(6) }
    private lateinit var progressBar : LottieAnimationView
    private lateinit var recyclerView: RecyclerView

    override fun getLayoutId(): Int = R.layout.fragment_search

    override fun onInternetRestored() {
        viewModel.manualRefresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.manualRefresh()
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) {
            swipeRefreshLayout.isRefreshing = it
        }

        recyclerView = view.findViewById(R.id.search_recyclerView)
        searchBar = view.findViewById(R.id.search_bar)
        progressBar = view.findViewById(R.id.progressBarSearch)

        adapter = EquipmentPagerAdapter(emptyList()) { equipmentId ->
            bindDetailFragment(equipmentId)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        observeViewModel(adapter, progressBar)
        setupSearchBarListener()

        val equipmentIdUnique = args.equipmentIdUnique
        if (equipmentIdUnique.isNotBlank()) {
            searchBar.setText(equipmentIdUnique)
            viewModel.onSearch(equipmentIdUnique, true)
        } else {
            viewModel.startAutoRefresh(10000L)
        }

        searchBar.requestFocus()
        searchBar.postDelayed({
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }
    private fun observeViewModel(adapter: EquipmentPagerAdapter, progressBar: LottieAnimationView) {
        viewModel.equipmentList.observe(viewLifecycleOwner) { equipmentList ->
            adapter.updateData(equipmentList)
        }

        viewModel.initialLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressBar.isVisible = true
                recyclerView.adapter = skeletonAdapter
            } else {
                if (progressBar.isVisible) {
                    progressBar.postDelayed({
                        progressBar.isVisible = false
                        recyclerView.adapter = adapter
                    }, 300)
                } else {
                    recyclerView.adapter = adapter
                }
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
            } else false
        }
    }
    private fun bindDetailFragment(equipmentId: Int) {
        val action = SearchFragmentDirections.actionSearchFragmentToEquipmentDetailFragment(equipmentId)
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