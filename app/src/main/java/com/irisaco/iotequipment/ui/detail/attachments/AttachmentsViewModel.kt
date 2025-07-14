package com.irisaco.iotequipment.ui.detail.attachments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.irisaco.iotequipment.data.model.Document
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay

class AttachmentsViewModel @AssistedInject constructor(
    @Assisted("equipmentId") private val equipmentId: Int?,
    @Assisted("recordId") private val recordId: Int?,
    private val repository: EquipmentRepository
) : ViewModel() {

    val documents: LiveData<List<Document>> = liveData(Dispatchers.IO) {
        var previousList: List<Document>? = null
        while (isActive) {
            val currentList = when {
                equipmentId != null && recordId == null -> {
                    repository.getDocumentForEquipment(equipmentId)
                }
                equipmentId != null && recordId != null -> {
                    val allRecords = repository.getRecordsForEquipment(equipmentId)
                    val record = allRecords.find { it.id == recordId }
                    record?.documents ?: emptyList()
                }

                else -> emptyList()
            }

            if (currentList != previousList) {
                emit(currentList)
                previousList = currentList
            }

            delay(5000)
        }
    }
}
