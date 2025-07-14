package com.irisaco.iotequipment.ui.detail.log.log_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogDetailViewModel @AssistedInject constructor (
    private var  repository : EquipmentRepository,
    @Assisted private val equipmentId : Int
) : ViewModel() {
    private val _deleteResult = MutableLiveData<Result<Boolean>>()
    val deleteResult: LiveData<Result<Boolean>> get() = _deleteResult
    fun getRecordById(recordId: Int): LiveData<Records?> = liveData(Dispatchers.IO) {
        val allRecords = repository.getRecordsForEquipment(equipmentId)
        val record = allRecords.find { it.id == recordId }
        emit(record)
    }
    fun deleteRecord(equipmentId: Int, recordId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteRecord(equipmentId, recordId)
                _deleteResult.postValue(Result.success(true))
            } catch (e: Exception) {
                _deleteResult.postValue(Result.failure(e))
            }
        }
    }
}


