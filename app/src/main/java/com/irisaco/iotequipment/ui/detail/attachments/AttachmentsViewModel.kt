package com.irisaco.iotequipment.ui.detail.attachments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irisaco.iotequipment.data.model.Document
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class AttachmentsViewModel @AssistedInject constructor(
    @Assisted("equipmentId") private val equipmentId: Int?,
    @Assisted("recordId") private val recordId: Int?,
    private val repository: EquipmentRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _documents = MutableLiveData<List<Document>>()
    val documents: LiveData<List<Document>> get() = _documents

    init {
        autoRefreshDocuments()
    }


    private fun autoRefreshDocuments() {
        viewModelScope.launch(Dispatchers.IO) {
            var previousList: List<Document>? = null
            while (isActive) {
                val currentList = fetchDocumentsInternal()

                if (previousList == null || !areListsEqual(previousList, currentList)) {
                    _documents.postValue(currentList)
                    previousList = currentList
                }

                delay(5000)
            }
        }
    }


    private fun areListsEqual(list1: List<Document>, list2: List<Document>): Boolean {
        if (list1.size != list2.size) return false
        return list1.zip(list2).all { (a, b) -> a == b }
    }
    fun refreshDocuments() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val currentList = fetchDocumentsInternal()
            _documents.postValue(currentList)
            _isLoading.postValue(false)
        }
    }


    private suspend fun fetchDocumentsInternal(): List<Document> {
        return when {
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
    }

    fun initialLoading() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            val result = fetchDocumentsInternal()
            _documents.postValue(result)

            _isLoading.postValue(false)
        }
    }

}

