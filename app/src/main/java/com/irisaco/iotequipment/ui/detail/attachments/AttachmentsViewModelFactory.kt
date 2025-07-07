package com.irisaco.iotequipment.ui.detail.attachments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irisaco.iotequipment.data.repository.EquipmentRepository

class AttachmentsViewModelFactory(
    private val equipmentId: Int?,
    private val recordId: Int?,
    private val repository: EquipmentRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AttachmentsViewModel(equipmentId, recordId, repository) as T
    }
}
