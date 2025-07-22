package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Version (

  @SerializedName("id"              ) var id              : Int?                 = null,
  @SerializedName("equipmentTypeId" ) var equipmentTypeId : Int?                 = null,
  @SerializedName("version"         ) var version         : String?              = null,
  @SerializedName("description"     ) var description     : String?              = null,
  @SerializedName("equipmentTypes"           ) var equipmentTypes           : ArrayList<EquipmentType>     = arrayListOf(),
  @SerializedName("documents"       ) var documents       : ArrayList<Document> = arrayListOf()

)