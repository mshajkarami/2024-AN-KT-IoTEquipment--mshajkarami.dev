package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Documents (

  @SerializedName("entityId"   ) var entityId   : Int?      = null,
  @SerializedName("entity"     ) var entity     : String?   = null,
  @SerializedName("documentId" ) var documentId : Int?      = null,
  @SerializedName("document"   ) var document   : Document? = Document()

)