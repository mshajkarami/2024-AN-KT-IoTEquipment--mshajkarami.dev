package com.irisaco.iotequipment.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.irisaco.iotequipment.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userRole = MutableLiveData<UserRole>()
    val userRole: LiveData<UserRole> = _userRole

    init {
        viewModelScope.launch {
            val account = authRepository.getCachedOrDbAccount()
            _userRole.value = account?.role ?: UserRole.UNKNOWN
        }
    }

    val canAddOrEditLog: LiveData<Boolean> = userRole.map { role ->
        role in listOf(UserRole.ADMIN, UserRole.ENGINEER)
    }
}

