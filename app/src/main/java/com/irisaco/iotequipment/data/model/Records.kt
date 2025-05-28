package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Records (

  @SerializedName("equipmentId"    ) var equipmentId    : Int?                 = null,
  @SerializedName("description"    ) var description    : String?              = null,
  @SerializedName("creationTime"   ) var creationTime   : String?              = null,
  @SerializedName("lastModifyTime" ) var lastModifyTime : String?              = null,
  @SerializedName("documents"      ) var documents      : ArrayList<Documents> = arrayListOf(),
  @SerializedName("id"             ) var id             : Int?                 = null

)