package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class RootResponse (

  @SerializedName("data"    ) var data    : Data?   = Data(),
  @SerializedName("status"  ) var status  : Int?    = null,
  @SerializedName("message" ) var message : String? = null

)