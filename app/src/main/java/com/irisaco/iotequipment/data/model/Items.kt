package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Items (

  @SerializedName("id"                      ) var id                      : Int?                    = null,
  @SerializedName("title"                   ) var title                   : String?                 = null,
  @SerializedName("equipmentVersionId"      ) var equipmentVersionId      : Int?                    = null,
  @SerializedName("productionDateTime"      ) var productionDateTime      : String?                 = null,
  @SerializedName("guid"                    ) var guid                    : String?                 = null,
  @SerializedName("description"             ) var description             : String?                 = null,
  @SerializedName("equipmentUniqueId"       ) var equipmentUniqueId       : String?                 = null,
  @SerializedName("creationTime"            ) var creationTime            : String?                 = null,
  @SerializedName("lastModifyTime"          ) var lastModifyTime          : String?                 = null,
  @SerializedName("version"                 ) var version                 : Version?                = Version(),
  @SerializedName("type"                    ) var equipmentType           : EquipmentType?          = EquipmentType(),
  @SerializedName("documents"               ) var documents               : ArrayList<Document>     = arrayListOf(),
  @SerializedName("records"                 ) var records                 : ArrayList<Records>      = arrayListOf(),
  @SerializedName("equipmentLOM"            ) var equipmentLOM            : ArrayList<EquipmentLOM> = arrayListOf(),
  @SerializedName("equipmentConfigurations" ) var equipmentConfigurations : List<EquipmentConfiguration>?                 = null,
  @SerializedName("imageUrl" ) var imageUrl : String?                 = null
)