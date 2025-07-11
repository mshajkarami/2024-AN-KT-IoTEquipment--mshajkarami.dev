package com.irisaco.iotequipment.ui.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irisaco.iotequipment.data.repository.AuthRepository

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val app: Application
) : ViewModelProvider.AndroidViewModelFactory(app) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(app, repository) as T
    }
}

