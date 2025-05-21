package com.irisaco.iotequipment.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.example.Equipment
import com.irisaco.iotequipment.data.repository.EquipmentRepository

class SearchViewModel (private val equipmentRepository: EquipmentRepository) : ViewModel()  {
    private val _devices = MutableLiveData<List<Equipment>>()
    val devices: LiveData<List<Equipment>> = _devices

    private val _filteredDevices = MutableLiveData<List<Equipment>>()
    val filteredDevices: LiveData<List<Equipment>> = _filteredDevices

    fun loadDevices() {
        val data = equipmentRepository.getEquipment()
        _devices.value = data
    }
}



