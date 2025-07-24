package com.irisaco.iotequipment.data.repository

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AuthState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            "auth_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private var cachedAuthState: AuthState? = null

    fun saveAuthState(authState: AuthState) {
        cachedAuthState = authState
        prefs.edit {
            putString("auth_state", authState.jsonSerializeString())
        }
    }

    fun readAuthState(): AuthState? {
        if (cachedAuthState != null) return cachedAuthState

        val serialized = prefs.getString("auth_state", null) ?: return null
        return try {
            AuthState.jsonDeserialize(serialized).also {
                cachedAuthState = it
            }
        } catch (e: Exception) {
            null
        }
    }

    fun clearAuthState() {
        cachedAuthState = null
        prefs.edit {
            remove("auth_state")
        }
    }
}