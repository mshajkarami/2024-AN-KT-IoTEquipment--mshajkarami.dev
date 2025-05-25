package com.irisaco.iotequipment.data.remote

import com.irisaco.iotequipment.data.model.Equipments
import retrofit2.Response
import retrofit2.http.GET

interface EquipmentApiService {
    @GET("/equipment")
    suspend fun getEquipment(): Response<Equipments>
}