package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class EquipmentVersion (

  @SerializedName("id"              ) var id              : Int?                 = null,
  @SerializedName("userCreatorId"   ) var userCreatorId   : Int?                 = null,
  @SerializedName("equipmentTypeId" ) var equipmentTypeId : Int?                 = null,
  @SerializedName("version"         ) var version         : String?              = null,
  @SerializedName("description"     ) var description     : String?              = null,
  @SerializedName("isDeleted"       ) var isDeleted       : Boolean?             = null,
  @SerializedName("documents"       ) var documents       : ArrayList<Documents> = arrayListOf(),
  @SerializedName("equipmentType"   ) var equipmentType   : EquipmentType?       = EquipmentType(),
  @SerializedName("user"            ) var user            : User?                = User(),
  @SerializedName("versionModel"    ) var versionModel    : String?              = null

)