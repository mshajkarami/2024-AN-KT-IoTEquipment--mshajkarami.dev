package com.irisaco.iotequipment.data.model

data class Equipments (
    val currentPage: Int,
    val totalPages: Int,
    val totalCount: Int,
    val items: ArrayList<Equipment>
)