package com.irisaco.iotequipment.di

import android.app.Application
import android.content.Context
import com.irisaco.iotequipment.utils.LocaleHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        val language = getSavedLanguageFromPreferences(base) ?: "fa"
        val contextWithLocale = base?.let { LocaleHelper.setLocale(it, language) }
        super.attachBaseContext(contextWithLocale)
    }

    private fun getSavedLanguageFromPreferences(context: Context?): String? {
        val prefs = context?.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return prefs?.getString("language", "fa")
    }
}

