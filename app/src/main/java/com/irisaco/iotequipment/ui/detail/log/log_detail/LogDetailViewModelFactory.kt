package com.irisaco.iotequipment.ui.detail.log.log_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class LogDetailViewModelFactory(
    private val assistedFactory: LogDetailViewModelAssistedFactory,
    private val equipmentId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return assistedFactory.create(equipmentId) as T
    }
}


