package com.irisaco.iotequipment.ui.detail.log.log_add

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

    private var  repository : EquipmentRepository

    private val _addRecordResult = MutableLiveData<Result<Boolean>>()
    val addRecordResult: LiveData<Result<Boolean>> = _addRecordResult

    init {
        val retrofitService = ApiClient.apiService
        repository = EquipmentRepository(retrofitService)
    }
    fun addRecord(equipmentId : Int, record: Records) {
        viewModelScope.launch {
            try {
                repository.addRecord(equipmentId,record)
                _addRecordResult.postValue(Result.success(true))
            } catch (e: Exception) {
                _addRecordResult.postValue(Result.failure(e))
            }
        }
    }
}