package com.irisaco.iotequipment.ui.auth
import android.util.Log
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
        val token = repository.getSavedToken()

        if (token == null) {
            Log.e("TokenAuthenticator", "No saved token found")
            return@runBlocking null
        }

        val newToken = repository.refreshToken(token.refreshToken)

        return@runBlocking if (newToken != null) {
            Log.d("TokenAuthenticator", "Token refreshed successfully")
            response.request.newBuilder()
                .header("Authorization", "Bearer ${newToken.accessToken}")
                .build()
        } else {
            Log.e("TokenAuthenticator", "Failed to refresh token")
            null
        }
    }
}



