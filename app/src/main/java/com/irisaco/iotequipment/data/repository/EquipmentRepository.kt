package com.irisaco.iotequipment.data.repository

import android.content.Context
import com.example.example.Equipment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.irisaco.iotequipment.data.model.Equipments
import com.irisaco.iotequipment.data.remote.ApiClient.apiService
import com.irisaco.iotequipment.data.remote.EquipmentApiService
import retrofit2.Response

// Responsible for fetching the main equipment data.
// Includes general info like title, description, production date, unique ID, and main photo.
// Use this to retrieve and manage the core equipment entity.

class EquipmentRepository(
    private val apiService: EquipmentApiService, private val context: Context

) {

    suspend fun getEquipments(useLocal: Boolean = false): Equipments {
        return if (useLocal) {
            getEquipmentsFromLocal()
        } else {
            apiService.getEquipment().body() ?: Equipments()
        }
    }

    private fun getEquipmentsFromLocal(): Equipments {
        val jsonString = context.assets.open("equipment.json")
            .bufferedReader()
            .use { it.readText() }
        return Gson().fromJson(jsonString, Equipments::class.java)
    }

    suspend fun getEquipmentById(id: Int): Equipment? {
        return try {
            val response = apiService.getEquipmentById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

}
