package com.example.example

import com.google.gson.annotations.SerializedName


data class Documents (

  @SerializedName("fileName" ) var fileName : String? = null,
  @SerializedName("fileUrl"  ) var fileUrl  : String? = null,
  @SerializedName("id"       ) var id       : Int?    = null

)