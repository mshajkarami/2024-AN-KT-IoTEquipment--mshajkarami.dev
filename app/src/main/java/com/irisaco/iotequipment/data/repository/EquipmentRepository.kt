package com.irisaco.iotequipment.data.repository

import android.content.Context
import com.google.gson.Gson
import com.irisaco.iotequipment.data.model.Document
import com.irisaco.iotequipment.data.model.Equipment
import com.irisaco.iotequipment.data.model.Items
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.model.RootResponse
import com.irisaco.iotequipment.data.remote.EquipmentApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Responsible for fetching the main equipment data.
// Includes general info like title, description, production date, unique ID, and main photo.
// Use this to retrieve and manage the core equipment entity.

class EquipmentRepository(
    private val apiService: EquipmentApiService,
    private val context: Context
) {

    suspend fun getEquipments(useLocal: Boolean = false): List<Items> {
        return if (useLocal) {
            getEquipmentsFromLocal()
        } else {
            getEquipmentsFromRemote()
        }
    }

    private suspend fun getEquipmentsFromRemote(): List<Items> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEquipment()
            if (response.isSuccessful) {
                response.body()?.data?.value?.items ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


    private fun getEquipmentsFromLocal(): List<Items> {
        return try {
            val jsonString = context.assets.open("equipment.json")
                .bufferedReader()
                .use { it.readText() }

            val root = Gson().fromJson(jsonString, RootResponse::class.java)

            root.data?.value?.items ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun getEquipmentById(id: Int): Items? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEquipmentById(id)
            if (response.isSuccessful) {
                response.body()?.data?.value?.items?.firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    suspend fun getEquipmentByIdFromList(id: Int): Items? = withContext(Dispatchers.IO) {
        getEquipments(useLocal = true).find { it.id == id }
    }

    suspend fun getRecordsForEquipment(id: Int, useLocal: Boolean): List<Records> = withContext(Dispatchers.IO) {
        val equipment = if (useLocal) getEquipmentByIdFromList(id) else getEquipmentById(id)
        equipment?.records ?: emptyList()
    }

    suspend fun getDocumentForEquipment(id: Int, useLocal: Boolean): List<Document> = withContext(Dispatchers.IO) {
        val equipment = if (useLocal) getEquipmentByIdFromList(id) else getEquipmentById(id)
        equipment?.documents?.mapNotNull { it.document } ?: emptyList()
    }

}

