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

        val account = repository.getCachedOrDbAccount()

        if (account == null) {
            Log.e("TokenAuthenticator", "No saved token found")
            return@runBlocking null
        }

        val newAccount = repository.refreshToken(account.refreshToken)

        return@runBlocking if (newAccount != null) {
            Log.d("TokenAuthenticator", "Token refreshed successfully")
            response.request.newBuilder()
                .header("Authorization", "Bearer ${newAccount.accessToken}")
                .build()
        } else {
            Log.e("TokenAuthenticator", "Failed to refresh token")
            null
        }
    }
}



