package com.irisaco.iotequipment.data.model

import com.google.gson.annotations.SerializedName


data class Data (
  @SerializedName("currentPage" ) var currentPage : Int? = null,
  @SerializedName("totalPages" ) var totalPages : Int? = null,
  @SerializedName("totalCount" ) var totalCount : Int? = null,
  @SerializedName("items"       ) var items       : ArrayList<Items> = arrayListOf()
)