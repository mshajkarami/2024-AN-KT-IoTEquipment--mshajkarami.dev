package com.irisaco.iotequipment.data.repository

import android.util.Log
import com.irisaco.iotequipment.data.model.Document
import com.irisaco.iotequipment.data.model.Items
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.remote.EquipmentApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

// Responsible for fetching the main equipment data.
// Includes general info like title, description, production date, unique ID, and main photo.
// Use this to retrieve and manage the core equipment entity.

@Singleton
class EquipmentRepository @Inject constructor(
    private val apiService: EquipmentApiService,
) {
    private var cachedItems: List<Items> = emptyList()

    suspend fun getEquipments(): List<Items> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEquipment()
            if (response.isSuccessful) {
                response.body()?.data?.items ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching equipment: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getEquipmentById(id: Int): Items? = withContext(Dispatchers.Default) {
        if (cachedItems.isEmpty()) {
            cachedItems = getEquipments()
        }

        return@withContext cachedItems.find { it.id == id }
    }

    suspend fun getRecordsForEquipment(id: Int): List<Records> =
        withContext(Dispatchers.IO) {
            val equipment = getEquipmentById(id)
            equipment?.records ?: emptyList()
        }

    suspend fun addRecord(record: Records): Records? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addOrUpdateRecord(record)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("API_ERROR", "Code: ${response.code()}, Message: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("EXCEPTION", "Error adding record", e)
            null
        }
    }


    suspend fun getDocumentForEquipment(id: Int): List<Document> =
        withContext(Dispatchers.IO) {
            val equipment = getEquipmentById(id)
            equipment?.documents?.forEach { doc ->
                Log.d("Repo", "Document from API: $doc")
            }

            equipment?.documents ?: emptyList()

        }

    suspend fun search(query: String, onlyById: Boolean): List<Items> = withContext(Dispatchers.Default) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return@withContext emptyList()

        if (cachedItems.isEmpty()) {
            cachedItems = getEquipments()
        }

        return@withContext if (onlyById) {
            cachedItems.filter {
                it.equipmentUniqueId?.contains(trimmedQuery, ignoreCase = true) == true
            }
        } else {
            emptyList()
        }
    }

    suspend fun updateRecord(record: Records): Records = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addOrUpdateRecord(record)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response")
            } else {
                throw Exception("API Error: ${response.code()} ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("API", "Add record failed", e)
            throw e
        }
    }

    suspend fun deleteRecord(equipmentId: Int, recordId: Int) {
        apiService.deleteRecord(equipmentId, recordId)
    }
    suspend fun uploadRecordDocument(
        equipmentId: Int,
        recordId: Int?,
        file: File
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val finalRecordId = recordId ?: -1

            val response = apiService.uploadRecordDocument(equipmentId, finalRecordId, body)
            if (response.isSuccessful) {
                Log.d("UPLOAD", "File uploaded successfully: ${response.body()}")
                true
            } else {
                Log.e("UPLOAD", "Failed to upload file: ${response.code()} ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UPLOAD", "Exception during file upload", e)
            false
        }
    }
}