package com.irisaco.iotequipment.data.repository

import com.irisaco.iotequipment.data.local.AuthTokenDao
import com.irisaco.iotequipment.data.local.AuthTokenEntity
import com.irisaco.iotequipment.data.model.KeycloakTokenResponse
import com.irisaco.iotequipment.data.remote.KeycloakAuthService
import retrofit2.Response

class AuthRepository(
    private val authService: KeycloakAuthService ,
    private val authTokenDao: AuthTokenDao
) {
    suspend fun login(username: String, password: String): Response<KeycloakTokenResponse> {
        return authService.login(
            clientId = "iem",
            username = username,
            password = password,
            clientSecret = "4VrcHu9lW4misnkCjdEuSknNCyZ4QTbx"
        )
    }
    suspend fun refreshToken(oldRefreshToken: String): KeycloakTokenResponse? {
        val response = authService.refreshToken(
            refreshToken = oldRefreshToken,
            clientId = "iem",
            clientSecret = "y2ga3RPSP1RU5zgmt6F1iIeBYbwOKEIs"
        )

        return if (response.isSuccessful) {
            response.body()?.also {
                authTokenDao.saveToken(
                    AuthTokenEntity(
                        id = 1,
                        accessToken = it.accessToken,
                        refreshToken = it.refreshToken,
                        expiresAt = System.currentTimeMillis() + it.expiresIn * 1000
                    )
                )
            }
        } else {
            authTokenDao.clearTokens()
            null
        }
    }

    suspend fun getSavedToken(): AuthTokenEntity? {
        return authTokenDao.getToken()
    }

    suspend fun saveToken(tokenResponse: KeycloakTokenResponse) {
        val expiresAt = System.currentTimeMillis() + tokenResponse.expiresIn * 1000
        val tokenEntity = AuthTokenEntity(
            id = 1,
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            expiresAt = expiresAt
        )
        authTokenDao.saveToken(tokenEntity)
    }

}
