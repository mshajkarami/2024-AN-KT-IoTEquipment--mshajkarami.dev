package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Equipment (

  @SerializedName("title"                   ) var title                   : String?                 = null,
  @SerializedName("equipmentVersionId"      ) var equipmentVersionId      : Int?                    = null,
  @SerializedName("productionDateTime"      ) var productionDateTime      : String?                 = null,
  @SerializedName("guid"                    ) var guid                    : String?                 = null,
  @SerializedName("description"             ) var description             : String?                 = null,
  @SerializedName("equipmentUniqueId"       ) var equipmentUniqueId       : String?                 = null,
  @SerializedName("creationTime"            ) var creationTime            : String?                 = null,
  @SerializedName("lastModifyTime"          ) var lastModifyTime          : String?                 = null,
  @SerializedName("version"                 ) var version                 : Version?                = Version(),
  @SerializedName("type"                    ) var type                    : Type?                   = Type(),
  @SerializedName("documents"               ) var documents               : ArrayList<String>       = arrayListOf(),
  @SerializedName("mainPhoto"               ) var mainPhoto               : String?                 = null,
  @SerializedName("records"                 ) var records                 : ArrayList<String>       = arrayListOf(),
  @SerializedName("equipmentLOM"            ) var equipmentLOM            : ArrayList<EquipmentLOM> = arrayListOf(),
  @SerializedName("equipmentConfigurations" ) var equipmentConfigurations : String?                 = null,
  @SerializedName("id"                      ) var id                      : Int?                    = null
)