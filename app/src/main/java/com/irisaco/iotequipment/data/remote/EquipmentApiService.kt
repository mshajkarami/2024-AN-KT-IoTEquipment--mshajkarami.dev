package com.irisaco.iotequipment.data.remote

import com.irisaco.iotequipment.data.model.RootResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EquipmentApiService {
    @GET("/equipment")
    suspend fun getEquipment(): Response<RootResponse>

    @GET("equipments/{id}")
    suspend fun getEquipmentById(@Path("id") id: Int): Response<RootResponse>
}