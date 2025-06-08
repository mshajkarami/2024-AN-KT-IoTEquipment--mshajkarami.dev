package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Group (

  @SerializedName("id"         ) var id         : Int?              = null,
  @SerializedName("name"       ) var name       : String?           = null,
  @SerializedName("isDeleted"  ) var isDeleted  : Boolean?          = null,
  @SerializedName("users"      ) var users      : ArrayList<String> = arrayListOf(),
  @SerializedName("groupAdmin" ) var groupAdmin : String?           = null

)