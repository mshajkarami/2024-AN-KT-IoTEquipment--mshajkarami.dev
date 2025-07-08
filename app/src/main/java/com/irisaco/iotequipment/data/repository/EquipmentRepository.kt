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

// Responsible for fetching the main equipment data.
// Includes general info like title, description, production date, unique ID, and main photo.
// Use this to retrieve and manage the core equipment entity.

class EquipmentRepository(
    private val apiService: EquipmentApiService,
) {

    suspend fun getEquipments(): List<Items> {
        return getEquipmentsFromRemote()
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

    suspend fun getEquipmentById(id: Int): Items? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEquipmentById(id)
            if (response.isSuccessful) {
                // TODO: if use server with complex Response need to fix
                response.body()
            } else {
                Log.e("API_ERROR", "Code: ${response.code()}, Message: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("EXCEPTION", "Error getting equipment by ID", e)

            null
        }
    }

    suspend fun getRecordsForEquipment(id: Int): List<Records> =
        withContext(Dispatchers.IO) {
            val equipment = getEquipmentById(id)
            equipment?.records ?: emptyList()
        }

    suspend fun addRecord(equipmentId: Int, record: Records): Records =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.addRecorde(equipmentId, record)
                if (response.isSuccessful) {
                    return@withContext response.body() ?: throw Exception("Empty response")
                } else {
                    throw Exception(
                        "API Error: ${response.code()} ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            } catch (e: Exception) {
                Log.e("API", "Add record failed", e)
                throw e
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

    suspend fun search(query: String, onlyById: Boolean): List<Items> {
        return try {
            val response = apiService.searchEquipment(query, onlyById)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateRecord(equipmentId: Int, recordId: Int, record: Records) {
        apiService.updateRecord(equipmentId, recordId, record)
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

