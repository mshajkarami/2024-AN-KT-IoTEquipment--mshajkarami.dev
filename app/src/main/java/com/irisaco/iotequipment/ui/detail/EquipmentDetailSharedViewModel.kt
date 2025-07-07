package com.irisaco.iotequipment.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irisaco.iotequipment.data.model.Records

class EquipmentDetailSharedViewModel : ViewModel() {

    val equipmentId = MutableLiveData<Int>()

    val recordEquipmentId = MutableLiveData<Int?>()

    val recordToEdit = MutableLiveData<Records>()



    fun setEquipmentId(id: Int) {
        if (equipmentId.value != id) {
            equipmentId.value = id
        }
    }

    fun setRecordEquipmentId(id: Int?) {
        recordEquipmentId.value = id
    }
}
