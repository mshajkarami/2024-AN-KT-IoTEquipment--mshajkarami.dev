package com.example.example

import com.google.gson.annotations.SerializedName


data class Type (

  @SerializedName("name"        ) var name        : String?           = null,
  @SerializedName("description" ) var description : String?           = null,
  @SerializedName("abberation"  ) var abberation  : String?           = null,
  @SerializedName("documents"   ) var documents   : ArrayList<String> = arrayListOf(),
  @SerializedName("id"          ) var id          : Int?              = null

)