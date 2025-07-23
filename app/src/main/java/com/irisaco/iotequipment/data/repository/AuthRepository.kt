package com.irisaco.iotequipment.data.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.irisaco.iotequipment.data.local.AccountDao
import com.irisaco.iotequipment.data.local.AccountEntity
import com.irisaco.iotequipment.data.remote.KeycloakAuthService
import com.irisaco.iotequipment.domain.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenResponse
import org.json.JSONObject
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AuthRepository @Inject constructor(
    private val accountDao: AccountDao,
    val appAuthManager: AppAuthManager,
    @ApplicationContext private val context: Context
)
{
    private var cachedAccount: AccountEntity? = null

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveBasicAccountInfoFromAppAuth() {
        val authState = appAuthManager.readAuthState() ?: return
        val accessToken = authState.accessToken ?: return

        val accountEntity = decodeUsernameFromToken(accessToken) ?: return
        val role = extractUserRoleFromToken(accessToken, clientId = "iem")

        val account = accountEntity.copy(
            isActive = true,
            role = role
        )

        accountDao.deactivateAllAccounts()
        accountDao.insertAccount(account)
        cachedAccount = account
    }




    suspend fun getValidAccessTokenOrNull(): String? {
        val authState = appAuthManager.readAuthState() ?: return null

        return suspendCoroutine { continuation ->
            val service = AuthorizationService(context)
            authState.performActionWithFreshTokens(service) { accessToken, _, ex ->
                if (ex != null) {
                    continuation.resume(null)
                } else {
                    continuation.resume(accessToken)
                }
            }
        }
    }

    suspend fun getActiveAccount(): AccountEntity? = accountDao.getActiveAccount()

    suspend fun clear() {
        accountDao.deactivateAllAccounts()
        appAuthManager.clearAuthState()
        cachedAccount = null
    }
    fun getCachedAccount(): AccountEntity? = cachedAccount
    suspend fun loadActiveAccountFromDb() {
        cachedAccount = accountDao.getActiveAccount()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun decodeUsernameFromToken(accessToken: String): AccountEntity? {
        return try {
            val payload = accessToken.split(".")[1]
            val decodedBytes = Base64.getUrlDecoder().decode(payload)
            val json = JSONObject(String(decodedBytes))

            val sub = json.getString("sub")
            val username = json.getString("preferred_username")
            val displayName = json.optString("name")
            val email = json.optString("email")
            val emailVerified = json.optBoolean("email_verified", false)
            val givenName = json.optString("given_name")
            val familyName = json.optString("family_name")

            val roles = mutableListOf<String>()
            val realmRoles = json.optJSONObject("realm_access")?.optJSONArray("roles")
            if (realmRoles != null) {
                for (i in 0 until realmRoles.length()) {
                    roles.add(realmRoles.getString(i))
                }
            }
            val resourceAccess = json.optJSONObject("resource_access")
            resourceAccess?.keys()?.forEach { client ->
                val clientRoles = resourceAccess.optJSONObject(client)?.optJSONArray("roles")
                if (clientRoles != null) {
                    for (i in 0 until clientRoles.length()) {
                        roles.add(clientRoles.getString(i))
                    }
                }
            }

            val groupsArray = json.optJSONArray("groups")
            val groups = mutableListOf<String>()
            if (groupsArray != null) {
                for (i in 0 until groupsArray.length()) {
                    groups.add(groupsArray.getString(i))
                }
            }

            AccountEntity(
                sub = sub,
                username = username,
                displayName = displayName,
                email = email,
                emailVerified = emailVerified,
                givenName = givenName,
                familyName = familyName,
                roles = roles,
                groups = if (groups.isEmpty()) null else groups,
                isActive = true
            )
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

        val account = accountDao.getActiveAccount()
        cachedAccount = account
        return account
    }
}