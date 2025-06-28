package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Records (

  @SerializedName("id"             ) var id             : Int?                 = null,
  @SerializedName("equipmentId"    ) var equipmentId    : Int?                 = null,
  @SerializedName("userCreatorId"  ) var userCreatorId  : Int?                 = null,
  @SerializedName("description"    ) var description    : String?              = null,
  @SerializedName("creationTime"   ) var creationTime   : String?              = null,
  @SerializedName("lastModifyTime" ) var lastModifyTime : String?              = null,
  @SerializedName("documents"      ) var documents      : ArrayList<Document> = arrayListOf(),
  @SerializedName("user"           ) var user           : User?                = User()

)