package com.irisaco.iotequipment.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irisaco.iotequipment.data.repository.AuthRepository

class RoleViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoleViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
