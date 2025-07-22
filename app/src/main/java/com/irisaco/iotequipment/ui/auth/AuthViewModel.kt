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

    fun startLoginIntent(): Intent {
        val config = AuthorizationServiceConfiguration(
            "https://account.irisaprime.ir/realms/dev/protocol/openid-connect/auth".toUri(),
            "https://account.irisaprime.ir/realms/dev/protocol/openid-connect/token".toUri()
        )

        val authRequest = AuthorizationRequest.Builder(
            config,
            "iem",
            ResponseTypeValues.CODE,
            "com.irisaco.iotequipment://oauth2redirect".toUri()
        ).setScope("email profile")
            .build()

        return authService.getAuthorizationRequestIntent(authRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleTokenExchange(intent: Intent, onDone: (Boolean, String?) -> Unit) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        if (response != null) {
            val request = TokenRequest.Builder(
                response.request.configuration,
                response.request.clientId
            )
                .setGrantType(GrantTypeValues.AUTHORIZATION_CODE)
                .setAuthorizationCode(response.authorizationCode)
                .setRedirectUri(response.request.redirectUri)
                .setCodeVerifier(response.request.codeVerifier)
                .build()

            val clientAuthentication = ClientSecretBasic("GN2HvCkONDIQOtgOm4pfTepqFv34fJL3")

            authService.performTokenRequest(request, clientAuthentication) { tokenResponse, ex ->
                viewModelScope.launch {
                    if (tokenResponse != null) {
                        val accessToken = tokenResponse.accessToken ?: ""
                        val refreshToken = tokenResponse.refreshToken ?: ""
                        val expiresAtMillis = tokenResponse.accessTokenExpirationTime ?: 0L
                        val currentTimeMillis = System.currentTimeMillis()
                        val expiresInSec = if (expiresAtMillis > currentTimeMillis) {
                            (expiresAtMillis - currentTimeMillis) / 1000
                        } else 3600L

                        repository.saveAccount(
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            expiresInSec = expiresInSec
                        )

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