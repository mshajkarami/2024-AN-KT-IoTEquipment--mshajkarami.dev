package com.irisaco.iotequipment.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irisaco.iotequipment.data.model.KeycloakTokenResponse
import com.irisaco.iotequipment.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _token = MutableLiveData<KeycloakTokenResponse?>()
    val token: LiveData<KeycloakTokenResponse?> = _token

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(username, password)
                if (response.isSuccessful) {
                    _token.value = response.body()
                } else {
                    _error.value = "Login failed: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            }
        }
    }
}

