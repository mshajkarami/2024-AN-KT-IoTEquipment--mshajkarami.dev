package com.irisaco.iotequipment.data.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.irisaco.iotequipment.data.local.AccountEntity
import com.irisaco.iotequipment.data.local.AppDatabase
import com.irisaco.iotequipment.data.remote.ApiClient.authService
import com.irisaco.iotequipment.domain.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.util.Base64
import javax.inject.Inject

class AuthRepository @Inject constructor(
    @ApplicationContext context: Context
){

    private val dao = AppDatabase.getInstance(context).accountDao()

    private var cachedAccount: AccountEntity? = null

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveAccount(
        accessToken: String,
        refreshToken: String,
        expiresInSec: Long
    ) {
        val username = decodeUsernameFromToken(accessToken)
        val role = extractUserRoleFromToken(accessToken, clientId = "iem-dev")
        if (username != null) {
            val expiresAt = System.currentTimeMillis() + expiresInSec * 1000
            val account = AccountEntity(
                username = username,
                displayName = username,
                profileImageUrl = null,
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresAt = expiresAt,
                isActive = true,
                role = role
            )
            dao.deactivateAllAccounts()
            dao.insertAccount(account)
            cachedAccount = account

            Log.d("AuthRepository", "Saving account for $username with role ${role.name}")

        } else {
            Log.e("AuthRepository", "Unable to decode username from access token")
        }
    }

    suspend fun getValidAccessTokenOrNull(): String? {
        val account = dao.getActiveAccount() ?: return null
        return if (account.expiresAt > System.currentTimeMillis()) {
            account.accessToken
        } else null
    }

    suspend fun getActiveAccount(): AccountEntity? = dao.getActiveAccount()

    suspend fun clear() = dao.deactivateAllAccounts()

    suspend fun refreshToken(refreshToken: String): AccountEntity? {
        val account = dao.getActiveAccount() ?: return null
        return try {
            val response = authService.refreshToken(
                grantType = "refresh_token",
                clientId = "iem-dev",
                refreshToken = account.refreshToken
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val newAccount = account.copy(
                        accessToken = body.accessToken,
                        refreshToken = body.refreshToken ?: account.refreshToken,
                        expiresAt = System.currentTimeMillis() + (body.expiresIn * 1000)
                    )
                    dao.insertAccount(newAccount)
                    Log.d("AuthRepository", "Token refreshed successfully.")
                    newAccount
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

    fun getCachedAccount(): AccountEntity? = cachedAccount

    suspend fun loadActiveAccountFromDb() {
        cachedAccount = dao.getActiveAccount()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun decodeUsernameFromToken(accessToken: String): String? {
        return try {
            val parts = accessToken.split(".")
            if (parts.size != 3) return null
            val payload = parts[1]
            val decodedBytes = Base64.getUrlDecoder().decode(payload)
            val json = JSONObject(String(decodedBytes))
            json.getString("preferred_username")
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extractUserRoleFromToken(accessToken: String, clientId: String): UserRole {
        return try {
            val payload = accessToken.split(".")[1]
            val decodedBytes = Base64.getUrlDecoder().decode(payload)
            val json = JSONObject(String(decodedBytes))
            val rolesArray = json
                .getJSONObject("resource_access")
                .getJSONObject(clientId)
                .getJSONArray("roles")

            val roles = mutableListOf<String>()
            for (i in 0 until rolesArray.length()) {
                roles.add(rolesArray.getString(i))
            }

            UserRole.fromAccount(roles)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error extracting role", e)
            UserRole.UNKNOWN
        }
    }


    suspend fun getCachedOrDbAccount(): AccountEntity? {
        if (cachedAccount != null) return cachedAccount

        val account = dao.getActiveAccount()
        cachedAccount = account
        return account
    }


}

