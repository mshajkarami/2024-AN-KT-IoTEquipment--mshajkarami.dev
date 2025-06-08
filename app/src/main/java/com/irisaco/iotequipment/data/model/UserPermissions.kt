package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class UserPermissions (

  @SerializedName("id"         ) var id         : Int?     = null,
  @SerializedName("userId"     ) var userId     : Int?     = null,
  @SerializedName("permission" ) var permission : Int?     = null,
  @SerializedName("isDeleted"  ) var isDeleted  : Boolean? = null

)