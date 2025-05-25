package com.irisaco.iotequipment.data.remote

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

object ApiClient {
    lateinit var retrofit: Retrofit

    val apiService: EquipmentApiService by lazy {
        retrofit.create(EquipmentApiService::class.java)
    }
}