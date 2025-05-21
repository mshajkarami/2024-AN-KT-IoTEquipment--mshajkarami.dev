package com.irisaco.iotequipment.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import com.irisaco.iotequipment.ui.search.SearchViewModel

class SearchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(EquipmentRepository(context)) as T
    }
}
