package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class EquipmentLOM (

  @SerializedName("isDeleted"   ) var isDeleted   : Boolean? = null,
  @SerializedName("name"        ) var name        : String?  = null,
  @SerializedName("quantity"    ) var quantity    : Int?     = null,
  @SerializedName("supplier"    ) var supplier    : String?  = null,
  @SerializedName("equipmentId" ) var equipmentId : Int?     = null,
  @SerializedName("equipment"   ) var equipment   : String?  = null,
  @SerializedName("id"          ) var id          : Int?     = null

)