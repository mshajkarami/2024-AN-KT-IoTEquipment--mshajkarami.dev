package com.irisaco.iotequipment.ui.detail.attachments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AttachmentsViewModelFactory(
    private val assistedFactory: AttachmentsViewModelAssistedFactory,
    private val equipmentId: Int?,
    private val recordId: Int?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return assistedFactory.create(equipmentId, recordId) as T
    }
}