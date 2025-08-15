package com.irisaco.iotequipment.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.irisaco.iotequipment.utils.LocaleHelper

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_settings", MODE_PRIVATE)

        val lang = prefs.getString("language", "fa") ?: "fa"
        val contextWithLocale = LocaleHelper.setLocale(newBase, lang)

        super.attachBaseContext(contextWithLocale)
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        when (prefs.getString("theme", "light")) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}