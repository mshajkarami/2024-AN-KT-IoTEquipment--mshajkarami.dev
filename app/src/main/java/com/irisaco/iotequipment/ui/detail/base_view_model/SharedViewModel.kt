package com.irisaco.iotequipment.ui.detail.base_view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irisaco.iotequipment.data.model.Records

class SharedViewModel : ViewModel() {

    val equipmentId = MutableLiveData<Int>()
    val recordId = MutableLiveData<Int?>()
    val recordToEdit = MutableLiveData<Records>()
    private val _downloadAllTrigger = MutableLiveData<Unit>()
    val downloadAllTrigger: LiveData<Unit> get() = _downloadAllTrigger
    private val _triggerRefresh = MutableLiveData<Unit>()
    val triggerRefresh: LiveData<Unit> get() = _triggerRefresh
    private val _triggerShareAll = MutableLiveData<Unit>()
    val triggerShareAll: LiveData<Unit> get() = _triggerShareAll

    fun setEquipmentId(id: Int) {
        if (equipmentId.value != id) {
            equipmentId.value = id
        }
    }

    fun setRecordEquipmentId(id: Int?) {
        recordId.value = id
    }
    fun triggerDownloadAll() {
        _downloadAllTrigger.value = Unit
    }


    fun triggerRefresh() {
        _triggerRefresh.value = Unit
    }

    fun triggerShareAll() {
        _triggerShareAll.value = Unit
    }

}