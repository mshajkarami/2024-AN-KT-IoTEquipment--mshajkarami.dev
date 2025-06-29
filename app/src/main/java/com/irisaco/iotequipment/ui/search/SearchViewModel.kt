package com.irisaco.iotequipment.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.irisaco.iotequipment.data.model.Items
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EquipmentRepository = EquipmentRepository(ApiClient.apiService)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _equipmentList = MutableLiveData<List<Items>>()
    val equipmentList: LiveData<List<Items>> get() = _equipmentList

    private var autoRefreshJob: Job? = null
    private var isSearching = false

    fun startAutoRefresh(intervalMillis: Long = 5000L) {
        if (autoRefreshJob != null) return

        autoRefreshJob = viewModelScope.launch {
            var isFirstLoad = true
            while (isActive) {
                if (!isSearching) {
                    if (isFirstLoad) {
                        _isLoading.postValue(true)
                    }

                    try {
                        val result = repository.getEquipments()
                        if (_equipmentList.value != result) {
                            _equipmentList.postValue(result)
                        }

                    } catch (e: Exception) {
                        _equipmentList.postValue(emptyList())
                    } finally {
                        if (isFirstLoad) {
                            _isLoading.postValue(false)
                            isFirstLoad = false
                        }
                    }
                }

                delay(intervalMillis)
            }
        }
    }



    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    fun clearSearchAndResumeAutoRefresh() {
        isSearching = false
        stopAutoRefresh()
        loadAllEquipments()
        startAutoRefresh()
    }

    private fun loadAllEquipments() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getEquipments()
                _equipmentList.value = result
            } catch (e: Exception) {
                _equipmentList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearch(query: String, onlyById: Boolean = false) {
        isSearching = true
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.search(query, onlyById)
                _equipmentList.value = result
            } catch (e: Exception) {
                _equipmentList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}





