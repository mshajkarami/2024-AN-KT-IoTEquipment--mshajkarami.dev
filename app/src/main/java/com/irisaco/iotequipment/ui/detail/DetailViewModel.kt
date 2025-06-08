package com.irisaco.iotequipment.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.irisaco.iotequipment.data.model.Document
import com.irisaco.iotequipment.data.model.Equipment
import com.irisaco.iotequipment.data.model.Items
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import kotlinx.coroutines.Dispatchers

class DetailViewModel(
    application: Application,
    private val equipmentId: Int
) : AndroidViewModel(application) {

    suspend fun getEquipmentById(equipmentId: Int): Items? {

        return repository.getEquipmentByIdFromList(equipmentId)

    }

    private lateinit var repository: EquipmentRepository

    private val _equipmentDetail = liveData(Dispatchers.IO) {
        val result = repository.getEquipmentById(equipmentId)
        emit(result)
    }

    val equipmentDetail: LiveData<Items?> get() = _equipmentDetail

    val equipmentRecords: LiveData<List<Records>> = liveData(Dispatchers.IO) {
        val records = repository.getRecordsForEquipment(equipmentId, true)
        emit(records)
    }

    val equipmentDocument: LiveData<List<Document>> = liveData(Dispatchers.IO) {
        val document = repository.getDocumentForEquipment(equipmentId, true)
        emit(document)
    }


    init {
        val retrofitService = ApiClient.apiService
        repository = EquipmentRepository(retrofitService, application.applicationContext)
    }
}