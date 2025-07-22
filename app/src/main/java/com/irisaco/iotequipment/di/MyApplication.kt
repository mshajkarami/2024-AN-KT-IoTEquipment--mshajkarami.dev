package com.irisaco.iotequipment.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.irisaco.iotequipment.data.dataStore.LanguageDataStore
import com.irisaco.iotequipment.data.local.AppDatabase
import com.irisaco.iotequipment.utils.LanguageUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        val lang = getSavedLanguageFromPreferences(base)
        val contextWithLocale = lang?.let { LanguageUtils.setLocale(base!!, it) } ?: base
        super.attachBaseContext(contextWithLocale)
    }

    private fun getSavedLanguageFromPreferences(context: Context?): String? {
        val prefs = context?.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return prefs?.getString("language", "fa")
    }
}

