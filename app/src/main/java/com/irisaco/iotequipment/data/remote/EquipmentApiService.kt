package com.irisaco.iotequipment.data.remote

import com.example.example.Equipment
import com.irisaco.iotequipment.data.model.Equipments
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EquipmentApiService {
    @GET("/equipment")
    suspend fun getEquipment(): Response<Equipments>

    @GET("equipments/{id}")
    suspend fun getEquipmentById(@Path("id") id: Int): Response<Equipment>

}