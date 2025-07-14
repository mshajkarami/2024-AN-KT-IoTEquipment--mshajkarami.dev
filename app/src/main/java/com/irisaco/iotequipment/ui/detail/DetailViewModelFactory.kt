package com.irisaco.iotequipment.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class DetailViewModelFactory @Inject constructor(
    private val assistedFactory: DetailViewModelAssistedFactory
) {
    fun create(equipmentId: Int): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(equipmentId) as T
            }
        }
    }
}
