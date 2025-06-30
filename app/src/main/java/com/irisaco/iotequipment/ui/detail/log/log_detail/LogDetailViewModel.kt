package com.irisaco.iotequipment.ui.detail.log.log_detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import kotlinx.coroutines.Dispatchers

class LogDetailViewModel(
    application: Application,
    private val equipmentId : Int
) : AndroidViewModel(application) {
    private var  repository : EquipmentRepository

    init {
        val retrofitService = ApiClient.apiService
        repository = EquipmentRepository(retrofitService)
    }

    fun getRecordById(recordId: Int): LiveData<Records?> = liveData(Dispatchers.IO) {
        val allRecords = repository.getRecordsForEquipment(equipmentId)
        val record = allRecords.find { it.id == recordId }
        emit(record)
    }
}


