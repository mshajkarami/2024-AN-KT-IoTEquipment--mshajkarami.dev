package com.irisaco.iotequipment

import android.app.Application
import androidx.room.Room
import com.irisaco.iotequipment.data.local.AppDatabase


class MyApp : Application() {

    companion object {
        lateinit var instance: MyApp
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
    }
}