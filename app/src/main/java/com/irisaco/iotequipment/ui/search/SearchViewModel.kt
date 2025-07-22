package com.irisaco.iotequipment.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.irisaco.iotequipment.data.model.Items
import com.irisaco.iotequipment.data.repository.EquipmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: EquipmentRepository
) :
    ViewModel() {


    private val _initialLoading = MutableLiveData<Boolean>()
    val initialLoading: LiveData<Boolean> get() = _initialLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val _equipmentList = MutableLiveData<List<Items>>()
    val equipmentList: LiveData<List<Items>> get() = _equipmentList

    private var autoRefreshJob: Job? = null
    private var isSearching = false

    fun startAutoRefresh(intervalMillis: Long = 10000L) {
        if (autoRefreshJob != null) return

        autoRefreshJob = viewModelScope.launch {
            var isFirstLoad = true
            while (isActive) {
                if (!isSearching) {
                    if (isFirstLoad) _initialLoading.postValue(true)
                    else _isRefreshing.postValue(true)

                    try {
                        val result = repository.getEquipments()
                        _equipmentList.postValue(result)
                    } catch (e: Exception) {
                        _equipmentList.postValue(emptyList())
                    } finally {
                        delay(500)
                        if (isFirstLoad) {
                            _initialLoading.postValue(false)
                            isFirstLoad = false
                        } else {
                            _isRefreshing.postValue(false)
                        }
                    }
                }

                delay(intervalMillis)
            }
        }
    }
    fun manualRefresh() {
        if (!isSearching) {
            viewModelScope.launch {
                _isRefreshing.value = true
                try {
                    val result = repository.getEquipments()
                    _equipmentList.value = result
                } catch (e: Exception) {
                    _equipmentList.value = emptyList()
                } finally {
                    delay(500)
                    _isRefreshing.value = false
                }
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
            _initialLoading.value = true
            val startTime = System.currentTimeMillis()
            try {
                val result = repository.getEquipments()
                _equipmentList.value = result
            } catch (e: Exception) {
                _equipmentList.value = emptyList()
            } finally {
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < 1000) delay(1000 - elapsed)
                _initialLoading.value = false
            }
        }
    }


    fun onSearch(query: String, onlyById: Boolean = false) {
        isSearching = true
        _initialLoading.value = true
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            try {
                val result = repository.search(query, onlyById)
                _equipmentList.value = result
            } catch (e: Exception) {
                _equipmentList.value = emptyList()
            } finally {
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < 1000) delay(1000 - elapsed)
                _initialLoading.value = false
            }
        }
    }

}