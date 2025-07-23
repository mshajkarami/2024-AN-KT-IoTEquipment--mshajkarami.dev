package com.irisaco.iotequipment.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.irisaco.iotequipment.data.repository.AuthRepository
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irisaco.iotequipment.data.local.AccountEntity
import com.irisaco.iotequipment.utils.UnsafeConnectionBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.TokenRequest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AuthRepository
) : ViewModel() {

    private val _isLoggedInLiveData = MutableLiveData<Boolean>()
    val isLoggedInLiveData: LiveData<Boolean> = _isLoggedInLiveData

    private val authService = AuthorizationService(
        context,
        AppAuthConfiguration.Builder()
            .setConnectionBuilder(UnsafeConnectionBuilder)
            .build()
    )

    private val serviceConfig = AuthorizationServiceConfiguration(
        "https://account.irisaprime.ir/realms/dev/protocol/openid-connect/auth".toUri(),
        "https://account.irisaprime.ir/realms/dev/protocol/openid-connect/token".toUri()
    )

    fun startLoginIntent(): Intent {
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            "iem",
            ResponseTypeValues.CODE,
            "com.irisaco.iotequipment://oauth2redirect".toUri()
        )
            .setScope("email profile")
            .build()

        return authService.getAuthorizationRequestIntent(authRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleTokenExchange(intent: Intent, onDone: (Boolean, String?) -> Unit) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        if (response != null) {
            val clientAuthentication = ClientSecretBasic("GN2HvCkONDIQOtgOm4pfTepqFv34fJL3")

            authService.performTokenRequest(
                response.createTokenExchangeRequest(),
                clientAuthentication
            ) { tokenResponse, ex ->

                viewModelScope.launch {
                    if (tokenResponse != null) {
                        repository.appAuthManager.saveAuthState(
                            AuthState(response, tokenResponse, ex)
                        )
                        repository.saveBasicAccountInfoFromAppAuth()
                        onDone(true, null)
                    } else {
                        onDone(false, ex?.errorDescription)
                    }
                }
            }
        } else {
            onDone(false, exception?.errorDescription)
        }
    }


    suspend fun isLoggedIn(): Boolean {
        return repository.getValidAccessTokenOrNull() != null
    }

    suspend fun getActiveAccount(): AccountEntity? {
        return repository.getActiveAccount()
    }

    suspend fun loadActiveAccountFromDb() {
        repository.loadActiveAccountFromDb()
    }

    fun checkLoginState() {
        viewModelScope.launch {
            loadActiveAccountFromDb()
            val isLoggedIn = isLoggedIn()
            _isLoggedInLiveData.postValue(isLoggedIn)
        }
    }
}