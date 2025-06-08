package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Message (

  @SerializedName("code"    ) var code    : String? = null,
  @SerializedName("message" ) var message : String? = null

)