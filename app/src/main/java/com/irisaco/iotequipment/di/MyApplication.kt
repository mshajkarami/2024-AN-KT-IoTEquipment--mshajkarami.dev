package com.irisaco.iotequipment.di

import android.app.Application
import androidx.room.Room
import com.irisaco.iotequipment.data.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application()
