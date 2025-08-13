package com.irisaco.iotequipment.di

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.irisaco.iotequipment.utils.LocaleHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        applySavedTheme()
    }

    override fun attachBaseContext(base: Context?) {
        val language = getSavedLanguageFromPreferences(base) ?: "fa"
        val contextWithLocale = base?.let { LocaleHelper.setLocale(it, language) }
        super.attachBaseContext(contextWithLocale)
    }

    private fun getSavedLanguageFromPreferences(context: Context?): String? {
        val prefs = context?.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return prefs?.getString("language", "fa")
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        when (prefs.getString("theme", "light")) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }




}

