package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class EquipmentType (

  @SerializedName("id"             ) var id             : Int?                 = null,
  @SerializedName("userCreatorId"  ) var userCreatorId  : Int?                 = null,
  @SerializedName("mainPhotoId"    ) var mainPhotoId    : Int?                 = null,
  @SerializedName("name"           ) var name           : String?              = null,
  @SerializedName("description"    ) var description    : String?              = null,
  @SerializedName("abberation"     ) var abberation     : String?              = null,
  @SerializedName("isDeleted"      ) var isDeleted      : Boolean?             = null,
  @SerializedName("creationTime"   ) var creationTime   : String?              = null,
  @SerializedName("lastModifyTime" ) var lastModifyTime : String?              = null,
  @SerializedName("documents"      ) var documents      : ArrayList<Documents> = arrayListOf(),
  @SerializedName("mainPhoto"      ) var mainPhoto      : MainPhoto?           = MainPhoto(),
  @SerializedName("user"           ) var user           : User?                = User()

)