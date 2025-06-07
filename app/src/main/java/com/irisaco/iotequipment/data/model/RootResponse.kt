package com.irisaco.iotequipment.data.model

data class RootResponse(
    val data: Equipments,
    val status: Int,
    val message: String
)
