package com.irisaco.iotequipment.ui.detail.attachments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.irisaco.iotequipment.data.model.Document
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import kotlinx.coroutines.Dispatchers

class AttachmentsViewModel(
    private val equipmentId: Int?,
    private val recordId: Int?,
    private val repository: EquipmentRepository
) : ViewModel() {

    val documents: LiveData<List<Document>> = liveData(Dispatchers.IO) {
        val result = when {
            equipmentId != null && recordId == null -> {
                repository.getDocumentForEquipment(equipmentId)
            }

            recordId != null && equipmentId != null -> {
                val allRecords = repository.getRecordsForEquipment(equipmentId)
                val record = allRecords.find { it.id == recordId }
                record?.documents ?: emptyList()
            }

            else -> emptyList()
        }

        emit(result)
    }
}
