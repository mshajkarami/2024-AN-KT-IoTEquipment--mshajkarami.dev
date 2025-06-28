package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Types (

  @SerializedName("id"          ) var id          : Int?                 = null,
  @SerializedName("name"        ) var name        : String?              = null,
  @SerializedName("description" ) var description : String?              = null,
  @SerializedName("abberation"  ) var abberation  : String?              = null,
  @SerializedName("mainPhoto"   ) var mainPhoto   : MainPhoto?           = MainPhoto(),
  @SerializedName("documents"   ) var documents   : ArrayList<Document> = arrayListOf()

)