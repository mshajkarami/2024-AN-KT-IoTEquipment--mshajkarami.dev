package com.irisaco.iotequipment.ui.auth

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
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
import com.irisaco.iotequipment.utils.UnsafeConnectionBuilder
import net.openid.appauth.AppAuthConfiguration

class AuthViewModel(
    application: Application,
    private val repository: AuthRepository
) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val authService = AuthorizationService(
        context,
        AppAuthConfiguration.Builder()
            .setConnectionBuilder(UnsafeConnectionBuilder)
            .build()
    )

    fun startLoginIntent(): Intent {
        val config = AuthorizationServiceConfiguration(
            "http://192.168.1.100:8080/realms/dev/protocol/openid-connect/auth".toUri(),
            "http://192.168.1.100:8080/realms/dev/protocol/openid-connect/token".toUri()
        )

        val authRequest = AuthorizationRequest.Builder(
            config,
            "iem-dev",
            ResponseTypeValues.CODE,
            "com.irisaco.iotequipment:/oauth2redirect".toUri()
        ).setScope("email profile")
            .build()

        return authService.getAuthorizationRequestIntent(authRequest)
    }

    fun handleTokenExchange(intent: Intent, onDone: (Boolean, String?) -> Unit) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        if (response != null) {
            val request = response.createTokenExchangeRequest()
            authService.performTokenRequest(request) { tokenResponse, ex ->
                viewModelScope.launch {
                    if (tokenResponse != null) {
                        val previousRefreshToken = repository.getSavedToken()?.refreshToken ?: ""
                        val expiresInMillis = tokenResponse.accessTokenExpirationTime ?: 0L
                        val currentTimeMillis = System.currentTimeMillis()
                        val expiresInSec = if (expiresInMillis > currentTimeMillis) {
                            (expiresInMillis - currentTimeMillis) / 1000
                        } else {
                            3600L
                        }

                        repository.saveToken(
                            accessToken = tokenResponse.accessToken ?: "",
                            refreshToken = tokenResponse.refreshToken ?: previousRefreshToken,
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

    suspend fun getValidAccessToken(): String? {
        return repository.getValidAccessTokenOrNull()
    }
    suspend fun isLoggedIn(): Boolean {
        return repository.getValidAccessTokenOrNull() != null
    }

}


