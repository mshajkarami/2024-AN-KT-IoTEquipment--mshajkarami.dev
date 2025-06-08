package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class User (

  @SerializedName("id"              ) var id              : Int?                       = null,
  @SerializedName("userKeyClockId"  ) var userKeyClockId  : String?                    = null,
  @SerializedName("groupId"         ) var groupId         : Int?                       = null,
  @SerializedName("lastLoginDate"   ) var lastLoginDate   : String?                    = null,
  @SerializedName("role"            ) var role            : Int?                       = null,
  @SerializedName("fullName"        ) var fullName        : String?                    = null,
  @SerializedName("creationTime"    ) var creationTime    : String?                    = null,
  @SerializedName("lastModifyTime"  ) var lastModifyTime  : String?                    = null,
  @SerializedName("isDeleted"       ) var isDeleted       : Boolean?                   = null,
  @SerializedName("userPermissions" ) var userPermissions : ArrayList<UserPermissions> = arrayListOf(),
  @SerializedName("group"           ) var group           : Group?                     = Group(),
  @SerializedName("roleName"        ) var roleName        : String?                    = null

)