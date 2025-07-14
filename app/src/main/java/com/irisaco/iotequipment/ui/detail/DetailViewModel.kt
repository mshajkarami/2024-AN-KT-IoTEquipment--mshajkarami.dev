package com.irisaco.iotequipment.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.irisaco.iotequipment.data.model.Items
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel @AssistedInject constructor(
    private val repository: EquipmentRepository,
    @Assisted private val equipmentId: Int
) : ViewModel() {
    val title = MutableLiveData<String>()
    val version = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    suspend fun getEquipmentById(equipmentId: Int): Items? {
        return repository.getEquipmentById(equipmentId)
    }
    private val _equipmentDetail = liveData(Dispatchers.IO) {
        val result = repository.getEquipmentById(equipmentId)
        emit(result)
    }
    val equipmentDetail: LiveData<Items?> get() = _equipmentDetail
    private val _equipmentRecords = MutableLiveData<List<Records>>()
    val equipmentRecords: LiveData<List<Records>> get() = _equipmentRecords
    fun loadEquipmentRecords() {
        viewModelScope.launch {
            try {
                val records = repository.getRecordsForEquipment(equipmentId)
                _equipmentRecords.postValue(records)
            } catch (e: Exception) {

            }
        }
    }

    init {
        viewModelScope.launch {
            val item = repository.getEquipmentById(equipmentId)
            item?.let {
                title.postValue(it.title ?: "-")
                version.postValue(it.version?.version ?: "-")
                type.postValue(it.type?.name ?: "-")
                description.postValue(it.description ?: "-")
            }
        }
    }
}