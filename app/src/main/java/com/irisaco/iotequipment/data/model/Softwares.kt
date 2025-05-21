package com.example.example

import com.google.gson.annotations.SerializedName


data class Softwares (

  @SerializedName("name"    ) var name    : String? = null,
  @SerializedName("version" ) var version : String? = null

)