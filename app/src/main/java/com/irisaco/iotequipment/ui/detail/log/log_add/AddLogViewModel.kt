package com.irisaco.iotequipment.ui.detail.log.log_add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import kotlinx.coroutines.launch

class AddLogViewModel(
) : ViewModel() {

    private var repository: EquipmentRepository

    private val _addRecordResult = MutableLiveData<Result<Boolean>>()
    val addRecordResult: LiveData<Result<Boolean>> = _addRecordResult

    private val _updateResult = MutableLiveData<Result<Boolean>>()
    val updateResult: LiveData<Result<Boolean>> = _updateResult

    init {
        val retrofitService = ApiClient.apiService
        repository = EquipmentRepository(retrofitService)
    }

    fun addRecord(equipmentId: Int, record: Records) {
        viewModelScope.launch {
            try {
                repository.addRecord(equipmentId, record)
                _addRecordResult.postValue(Result.success(true))
            } catch (e: Exception) {
                _addRecordResult.postValue(Result.failure(e))
            }
        }
    }

    fun updateRecord(equipmentId: Int, recordId: Int, updatedRecord: Records) {
        viewModelScope.launch {
            try {
                Log.d("AddLogViewModel", "Sending update request for recordId=$recordId")
                repository.updateRecord(equipmentId, recordId, updatedRecord)
                _updateResult.postValue(Result.success(true))
            } catch (e: Exception) {
                _updateResult.postValue(Result.failure(e))
            }
        }
    }

}