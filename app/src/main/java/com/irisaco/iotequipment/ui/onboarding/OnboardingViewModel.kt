package com.irisaco.iotequipment.ui.onboarding

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.core.content.edit
import com.irisaco.iotequipment.utils.TemporarySettings.KEY_LANGUAGE
import com.irisaco.iotequipment.utils.TemporarySettings.KEY_THEME
import com.irisaco.iotequipment.utils.TemporarySettings.PREFS_NAME

class OnboardingViewModel(
    private val app: Application
) : AndroidViewModel(app) {



    private val prefs by lazy {
        app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val _language = MutableLiveData<String>(getSavedLanguage())
    val language: LiveData<String> get() = _language

    private val _theme = MutableLiveData<String>(getSavedTheme())
    val theme: LiveData<String> get() = _theme

    private fun getSavedLanguage(): String =
        prefs.getString(KEY_LANGUAGE, "fa") ?: "fa"

    private fun getSavedTheme(): String =
        prefs.getString(KEY_THEME, "light") ?: "light"

    fun setLanguage(lang: String) {
        if (lang != _language.value) {
            prefs.edit { putString(KEY_LANGUAGE, lang) }
            _language.value = lang
        }
    }

    fun setTheme(themeName: String) {
        if (themeName != _theme.value) {
            _theme.value = themeName
            prefs.edit { putString(KEY_THEME, themeName) }
            applyTheme(themeName)
        }
    }

    private fun applyTheme(themeName: String) {
        when (themeName) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}