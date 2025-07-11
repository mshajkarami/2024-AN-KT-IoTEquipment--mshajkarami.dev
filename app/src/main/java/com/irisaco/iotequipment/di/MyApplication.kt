package com.irisaco.iotequipment.di

import android.app.Application
import androidx.room.Room
import com.irisaco.iotequipment.data.local.AppDatabase
import com.irisaco.iotequipment.data.remote.ApiClient

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
            private set

        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "iot_equipment_db"
        ).build()

        ApiClient.init(applicationContext)

    }
}