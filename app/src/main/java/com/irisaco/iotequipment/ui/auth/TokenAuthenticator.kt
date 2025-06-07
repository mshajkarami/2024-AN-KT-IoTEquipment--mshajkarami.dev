package com.irisaco.iotequipment.ui.auth
import com.irisaco.iotequipment.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route


class TokenAuthenticator(
    private val repository: AuthRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? = runBlocking {

        val token = repository.getSavedToken() ?: return@runBlocking null

        if (token.expiresAt > System.currentTimeMillis()) return@runBlocking null

        val newToken = repository.refreshToken(token.refreshToken) ?: return@runBlocking null

        response.request.newBuilder()
            .header("Authorization", "Bearer ${newToken.accessToken}")
            .build()
    }
}


