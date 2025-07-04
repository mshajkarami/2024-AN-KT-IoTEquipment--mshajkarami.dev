package com.irisaco.iotequipment.ui.detail

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Document
import com.irisaco.iotequipment.data.model.Equipment
import com.irisaco.iotequipment.data.model.Items
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class DetailViewModel(
    application: Application,
    private val equipmentId: Int
) : AndroidViewModel(application) {

    val title = MutableLiveData<String>()
    val version = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val description = MutableLiveData<String>()




    suspend fun getEquipmentById(equipmentId: Int): Items? {

        return repository.getEquipmentById(equipmentId)

    }

    private lateinit var repository: EquipmentRepository

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




    val equipmentDocument: LiveData<List<Document>> = liveData(Dispatchers.IO) {
        val document = repository.getDocumentForEquipment(equipmentId)
        emit(document)
    }

    init {
        val retrofitService = ApiClient.apiService
        repository = EquipmentRepository(retrofitService)

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