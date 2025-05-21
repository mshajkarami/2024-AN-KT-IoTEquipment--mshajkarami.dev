package com.example.example

import com.google.gson.annotations.SerializedName


data class EquipmentDetail (

  @SerializedName("os"        ) var os        : String?              = null,
  @SerializedName("osVersion" ) var osVersion : String?              = null,
  @SerializedName("firmware"  ) var firmware  : String?              = null,
  @SerializedName("lom"       ) var lom       : ArrayList<String>    = arrayListOf(),
  @SerializedName("softwares" ) var softwares : ArrayList<Softwares> = arrayListOf(),
  @SerializedName("id"        ) var id        : Int?                 = null

)