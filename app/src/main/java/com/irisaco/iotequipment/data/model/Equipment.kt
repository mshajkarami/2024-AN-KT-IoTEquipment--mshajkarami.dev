package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Equipment (

  @SerializedName("id"                       ) var id                       : Int?                                = null,
  @SerializedName("userCreatorId"            ) var userCreatorId            : Int?                                = null,
  @SerializedName("title"                    ) var title                    : String?                             = null,
  @SerializedName("equipmentVersionId"       ) var equipmentVersionId       : Int?                                = null,
  @SerializedName("productionDateTime"       ) var productionDateTime       : String?                             = null,
  @SerializedName("guid"                     ) var guid                     : String?                             = null,
  @SerializedName("description"              ) var description              : String?                             = null,
  @SerializedName("isDeleted"                ) var isDeleted                : Boolean?                            = null,
  @SerializedName("creationTime"             ) var creationTime             : String?                             = null,
  @SerializedName("lastModifyTime"           ) var lastModifyTime           : String?                             = null,
  @SerializedName("equipmentVersion"         ) var equipmentVersion         : EquipmentVersion?                   = EquipmentVersion(),
  @SerializedName("records"                  ) var records                  : ArrayList<Records>                  = arrayListOf(),
  @SerializedName("documents"                ) var documents                : ArrayList<Document>                = arrayListOf(),
  @SerializedName("equipmentConfigurations"  ) var equipmentConfigurations  : String?                             = null,
  @SerializedName("equipmentLoms"            ) var equipmentLoms            : ArrayList<String>                   = arrayListOf(),
  @SerializedName("user"                     ) var user                     : User?                               = User(),
  @SerializedName("configurationsDictionary" ) var configurationsDictionary : ArrayList<EquipmentConfiguration> = arrayListOf(),
  @SerializedName("equipmentUniqueId"        ) var equipmentUniqueId        : String?                             = null

)