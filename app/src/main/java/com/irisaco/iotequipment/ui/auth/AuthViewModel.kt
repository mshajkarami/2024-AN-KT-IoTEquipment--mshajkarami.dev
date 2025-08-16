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
import com.irisaco.iotequipment.BuildConfig
import com.irisaco.iotequipment.data.local.AccountEntity
import com.irisaco.iotequipment.utils.UnsafeConnectionBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.ClientSecretBasic
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AuthRepository
) : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn


    private val _authResult = MutableLiveData<Result<Unit>>()
    val authResult: LiveData<Result<Unit>> = _authResult


    private val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean> = _networkStatus

    fun updateNetworkStatus(isConnected: Boolean) {
        _networkStatus.value = isConnected
    }

    private val authService: AuthorizationService by lazy {
        AuthorizationService(
            context,
            AppAuthConfiguration.Builder()
                .setConnectionBuilder(UnsafeConnectionBuilder)
                .build()
        )
    }

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
            .setPrompt("login")
            .build()

        return authService.getAuthorizationRequestIntent(authRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleTokenExchange(intent: Intent) {

        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        if (response == null) {
            _authResult.postValue(Result.failure(Exception(exception?.errorDescription)))
            return
        }

        val clientAuthentication = ClientSecretBasic(BuildConfig.CLIENT_SECRET)
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
                    checkLoginState()
                    _authResult.postValue(Result.success(Unit))
                } else {
                    _authResult.postValue(Result.failure(Exception(ex?.errorDescription)))
                }
            }
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
            repository.loadActiveAccountFromDb()
            val isLoggedIn = repository.getValidAccessTokenOrNull() != null
            _isLoggedIn.postValue(isLoggedIn)
        }
    }

    suspend fun getDisplayName(): String? {
        val account = repository.getActiveAccount()
        return account?.displayName
    }

    suspend fun getUserName(): String? {
        val account = repository.getActiveAccount()
        return account?.username
    }

    suspend fun getEmailName(): String? {
        val account = repository.getActiveAccount()
        return account?.email
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            repository.clear()
            _isLoggedIn.postValue(false)
            onLogoutComplete()
        }
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}