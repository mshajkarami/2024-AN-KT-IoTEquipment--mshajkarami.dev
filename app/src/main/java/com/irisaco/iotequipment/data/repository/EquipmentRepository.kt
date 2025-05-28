package com.irisaco.iotequipment.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.irisaco.iotequipment.data.model.Equipment
import com.irisaco.iotequipment.data.model.Equipments
import com.irisaco.iotequipment.data.remote.ApiClient.apiService
import com.irisaco.iotequipment.data.remote.EquipmentApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

// Responsible for fetching the main equipment data.
// Includes general info like title, description, production date, unique ID, and main photo.
// Use this to retrieve and manage the core equipment entity.

class EquipmentRepository(
    private val apiService: EquipmentApiService,
    private val context: Context
) {

    suspend fun getEquipments(useLocal: Boolean = false): Equipments {
        return if (useLocal) {
            getEquipmentsFromLocal()
        } else {
            getEquipmentsFromRemote()
        }
    }

    private suspend fun getEquipmentsFromRemote(): Equipments = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEquipment()
            response.body() ?: Equipments()
        } catch (e: Exception) {
            Equipments()
        }
    }

    private fun getEquipmentsFromLocal(): Equipments {
        return try {
            val jsonString = context.assets.open("equipment.json")
                .bufferedReader()
                .use { it.readText() }

            Gson().fromJson(jsonString, Equipments::class.java)
        } catch (e: Exception) {
            Equipments()
        }
    }

    suspend fun getEquipmentById(id: Int): Equipment? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEquipmentById(id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getEquipmentByIdFromList(id: Int): Equipment? = withContext(Dispatchers.IO) {
        getEquipments(useLocal = true).find { it.id == id }
    }
}

