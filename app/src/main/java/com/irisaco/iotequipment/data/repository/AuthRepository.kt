package com.irisaco.iotequipment.data.repository

import android.content.Context
import android.util.Log
import com.irisaco.iotequipment.data.local.AppDatabase
import com.irisaco.iotequipment.data.local.AuthTokenDao
import com.irisaco.iotequipment.data.local.AuthTokenEntity
import com.irisaco.iotequipment.data.model.KeycloakTokenResponse
import com.irisaco.iotequipment.data.remote.ApiClient.authService
import com.irisaco.iotequipment.data.remote.KeycloakAuthService
import retrofit2.Response

class AuthRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).authTokenDao()

    suspend fun saveToken(accessToken: String, refreshToken: String, expiresInSec: Long) {
        val expiresAt = System.currentTimeMillis() + expiresInSec * 1000
        val token = AuthTokenEntity(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresAt = expiresAt
        )
        dao.insert(token)
        cachedToken = token
    }

    suspend fun getValidAccessTokenOrNull(): String? {
        val token = dao.getToken() ?: return null
        return if (token.expiresAt > System.currentTimeMillis()) {
            token.accessToken
        } else null
    }

    suspend fun getToken(): AuthTokenEntity? = dao.getToken()

    suspend fun clear() = dao.clear()

    suspend fun getSavedToken(): AuthTokenEntity? = dao.getToken()

    suspend fun refreshToken(refreshToken: String): AuthTokenEntity? {
        return try {
            val response = authService.refreshToken(
                grantType = "refresh_token",
                clientId = "iem-dev",
                refreshToken = refreshToken
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val newToken = AuthTokenEntity(
                        accessToken = body.accessToken,
                        refreshToken = body.refreshToken ?: refreshToken,
                        expiresAt = System.currentTimeMillis() + (body.expiresIn * 1000)
                    )
                    dao.insert(newToken)
                    Log.d("AuthRepository", "Token refreshed successfully.")
                    newToken
                } else {
                    Log.e("AuthRepository", "Response body is null.")
                    null
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "Refresh failed: ${response.code()} - $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Refresh exception", e)
            null
        }
    }


    private var cachedToken: AuthTokenEntity? = null

    fun getCachedToken(): AuthTokenEntity? = cachedToken

    suspend fun loadTokenFromDb() {
        cachedToken = dao.getToken()
    }
}
