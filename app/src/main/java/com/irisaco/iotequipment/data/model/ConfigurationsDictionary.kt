package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class ConfigurationsDictionary (

  @SerializedName("additionalProp1" ) var additionalProp1 : String? = null,
  @SerializedName("additionalProp2" ) var additionalProp2 : String? = null,
  @SerializedName("additionalProp3" ) var additionalProp3 : String? = null

)