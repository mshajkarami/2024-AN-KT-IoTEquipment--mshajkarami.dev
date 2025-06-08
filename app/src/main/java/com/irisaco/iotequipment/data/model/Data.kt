package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("isSuccess" ) var isSuccess : Boolean? = null,
  @SerializedName("isFailure" ) var isFailure : Boolean? = null,
  @SerializedName("status"    ) var status    : Int?     = null,
  @SerializedName("value"     ) var value     : Value?   = Value(),
  @SerializedName("message"   ) var message   : Message? = Message()

)