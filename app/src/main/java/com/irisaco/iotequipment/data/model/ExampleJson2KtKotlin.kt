package com.example.example

import com.google.gson.annotations.SerializedName


data class ExampleJson2KtKotlin (

  @SerializedName("title"              ) var title              : String?            = null,
  @SerializedName("equipmentVersionId" ) var equipmentVersionId : Int?               = null,
  @SerializedName("productionDateTime" ) var productionDateTime : String?            = null,
  @SerializedName("guid"               ) var guid               : String?            = null,
  @SerializedName("description"        ) var description        : String?            = null,
  @SerializedName("equipmentUniqueId"  ) var equipmentUniqueId  : String?            = null,
  @SerializedName("creationTime"       ) var creationTime       : String?            = null,
  @SerializedName("lastModifyTime"     ) var lastModifyTime     : String?            = null,
  @SerializedName("version"            ) var version            : Version?           = Version(),
  @SerializedName("type"               ) var type               : Type?              = Type(),
  @SerializedName("equipmentDetail"    ) var equipmentDetail    : EquipmentDetail?   = EquipmentDetail(),
  @SerializedName("documents"          ) var documents          : ArrayList<String>  = arrayListOf(),
  @SerializedName("mainPhoto"          ) var mainPhoto          : MainPhoto?         = MainPhoto(),
  @SerializedName("records"            ) var records            : ArrayList<Records> = arrayListOf(),
  @SerializedName("id"                 ) var id                 : Int?               = null

)