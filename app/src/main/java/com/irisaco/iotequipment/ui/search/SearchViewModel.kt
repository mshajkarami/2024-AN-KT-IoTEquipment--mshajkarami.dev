package com.irisaco.iotequipment.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.irisaco.iotequipment.data.model.Equipments
import com.irisaco.iotequipment.data.remote.EquipmentApiService
import com.irisaco.iotequipment.data.remote.RetrofitInstance
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import kotlinx.coroutines.Dispatchers

class SearchViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private lateinit var repository: EquipmentRepository
    private val _equipmentList = liveData(Dispatchers.IO) {
        val result = repository.getEquipments(true)
        emit(result)
    }
    val equipmentList: LiveData<Equipments> get() = _equipmentList
    init {
        val retrofitService = RetrofitInstance.getRetrofitInstance().create(EquipmentApiService::class.java)
        repository = EquipmentRepository(retrofitService,application.applicationContext)
    }
}



