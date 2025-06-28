package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Document (

  @SerializedName("id"                        ) var id                        : Int?                              = null,
  @SerializedName("userCreatorId"             ) var userCreatorId             : Int?                              = null,
  @SerializedName("fileName"                  ) var fileName                  : String?                           = null,
  @SerializedName("fileUrl"                   ) var fileUrl                   : String?                           = null,
  @SerializedName("type"                      ) var type                      : String?                           = null,
  @SerializedName("use"                       ) var use                       : Boolean?                          = null,
  @SerializedName("isDeleted"                 ) var isDeleted                 : Boolean?                          = null,
  @SerializedName("creationTime"              ) var creationTime              : String?                           = null,
  @SerializedName("lastModifyTime"            ) var lastModifyTime            : String?                           = null,
  @SerializedName("equipmentTypeDocuments"    ) var equipmentTypeDocuments    : ArrayList<EquipmentTypeDocuments> = arrayListOf(),
  @SerializedName("equipmentVersionDocuments" ) var equipmentVersionDocuments : ArrayList<String>                 = arrayListOf(),
  @SerializedName("equipmentDocuments"        ) var equipmentDocuments        : ArrayList<String>                 = arrayListOf(),
  @SerializedName("recordDocuments"           ) var recordDocuments           : ArrayList<String>                 = arrayListOf(),
  @SerializedName("user"                      ) var user                      : User?                             = User()

)