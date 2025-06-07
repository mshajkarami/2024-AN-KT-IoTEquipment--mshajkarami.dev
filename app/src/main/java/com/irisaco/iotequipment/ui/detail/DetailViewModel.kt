package com.irisaco.iotequipment.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.irisaco.iotequipment.data.model.Equipment
import com.irisaco.iotequipment.data.model.Record
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import kotlinx.coroutines.Dispatchers

class DetailViewModel(
    application: Application,
    private val equipmentId: Int
) : AndroidViewModel(application) {

    suspend fun getEquipmentById(equipmentId: Int): Equipment? {

        return repository.getEquipmentByIdFromList(equipmentId)

    }

    private lateinit var repository: EquipmentRepository

    private val _equipmentDetail = liveData(Dispatchers.IO) {
        val result = repository.getEquipmentById(equipmentId)
        emit(result)
    }

    val equipmentDetail: LiveData<Equipment?> get() = _equipmentDetail

    val equipmentRecord: LiveData<List<Record>> = liveData(Dispatchers.IO) {
        val records = repository.getRecordsForEquipment(equipmentId, true)
        emit(records)
    }


    init {
        val retrofitService = ApiClient.apiService
        repository = EquipmentRepository(retrofitService, application.applicationContext)
    }
}