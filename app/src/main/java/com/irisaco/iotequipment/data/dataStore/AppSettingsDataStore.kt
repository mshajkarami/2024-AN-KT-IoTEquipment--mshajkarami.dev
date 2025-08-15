package com.irisaco.iotequipment.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object AppSettingsDataStore {
    private const val PREFERENCE_NAME = "settings"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)
    private val LANGUAGE_KEY = stringPreferencesKey("app_lang")
    private val Theme_KEY = stringPreferencesKey("app_theme")

    suspend fun saveLanguage(context: Context, lang: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = lang
        }
    }

    suspend fun getLanguage(context: Context): String {
        val preferences = context.dataStore.data.first()
        return preferences[LANGUAGE_KEY] ?: "fa"
    }

    fun getLanguageFlow(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[LANGUAGE_KEY] ?: "fa"
        }
    }

    suspend fun saveTheme(context: Context, theme: String) {
        context.dataStore.edit { preferences ->
            preferences[Theme_KEY] = theme
        }
    }

    suspend fun getTheme(context: Context): String {
        val preferences = context.dataStore.data.first()
        return preferences[Theme_KEY] ?: "light"
    }

    fun getThemeFlow(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[Theme_KEY] ?: "light"
        }
    }
}
