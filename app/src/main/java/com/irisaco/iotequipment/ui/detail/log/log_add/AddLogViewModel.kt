package com.irisaco.iotequipment.ui.detail.log.log_add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddLogViewModel @Inject constructor(
    private var repository: EquipmentRepository
) : ViewModel() {
    private val _addRecordResult = MutableLiveData<Result<Boolean>>()
    val addRecordResult: LiveData<Result<Boolean>> = _addRecordResult
    private val _updateResult = MutableLiveData<Result<Boolean>>()
    val updateResult: LiveData<Result<Boolean>> = _updateResult
    private val _uploadState = MutableLiveData<Result<Boolean>>()
    val uploadState: LiveData<Result<Boolean>> = _uploadState

    fun addRecord(record: Records) {
        viewModelScope.launch {
            try {
                repository.addRecord(record)
                _addRecordResult.postValue(Result.success(true))
            } catch (e: Exception) {
                _addRecordResult.postValue(Result.failure(e))
            }
        }
    }
    fun updateRecord(updatedRecord: Records) {
        viewModelScope.launch {
            try {
                Log.d("AddLogViewModel", "Sending update request for recordId=${updatedRecord.id}")
                repository.updateRecord(updatedRecord)
                _updateResult.postValue(Result.success(true))
            } catch (e: Exception) {
                _updateResult.postValue(Result.failure(e))
            }
        }
    }
    fun uploadDocument(equipmentId: Int, recordId: Int?, file: File) {
        viewModelScope.launch {
            try {
                val success = repository.uploadRecordDocument(equipmentId, recordId, file)
                if (success) {
                    _uploadState.postValue(Result.success(true))
                } else {
                    _uploadState.postValue(Result.failure(Exception("آپلود ناموفق بود")))
                }
            } catch (e: Exception) {
                _uploadState.postValue(Result.failure(e))
            }
        }
    }
}