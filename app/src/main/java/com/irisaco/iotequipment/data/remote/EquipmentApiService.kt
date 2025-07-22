package com.irisaco.iotequipment.data.remote

import com.irisaco.iotequipment.data.model.Items
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.model.RootResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface EquipmentApiService {
    @GET("api/Equipment/GetAll")
    suspend fun getEquipment(): Response<RootResponse>

    @GET("api/Equipment/{id}")
    suspend fun getEquipmentById(@Path("id") id: Int): Response<Items>

    @GET("api/Equipment/search")
    suspend fun searchEquipment(
        @Query("q") query: String,
        @Query("equipmentIdOnly") equipmentIdOnly: Boolean = false
    ): Response<List<Items>>

    @POST("api/Equipment/{id}/records")
    suspend fun addRecorde(@Path("id") id: Int,@Body record: Records): Response<Records>

    @DELETE("equipment/{equipmentId}/records/{recordId}")
    suspend fun deleteRecord(
        @Path("equipmentId") equipmentId: Int,
        @Path("recordId") recordId: Int
    ): Response<Unit>

    @POST("api/Record/CreateOrUpdate")
    suspend fun addOrUpdateRecord(
        @Body record: Records
    ): Response<Records>


    @Multipart
    @POST("equipment/{equipmentId}/records/{recordId}/upload")
    suspend fun uploadRecordDocument(
        @Path("equipmentId") equipmentId: Int,
        @Path("recordId") recordId: Int?,
        @Part file: MultipartBody.Part
    ): Response<String>

}