package com.irisaco.iotequipment.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EquipmentDetailSharedViewModel : ViewModel() {
    val equipmentId = MutableLiveData<Int>()
}
