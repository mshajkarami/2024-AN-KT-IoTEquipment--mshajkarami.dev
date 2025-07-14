package com.irisaco.iotequipment.di.module

import android.content.Context
import androidx.room.Room
import com.irisaco.iotequipment.data.local.AccountDao
import com.irisaco.iotequipment.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "iot_equipment_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao {
        return db.accountDao()
    }
}