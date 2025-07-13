package com.irisaco.iotequipment.utils

interface  NetworkStatusListener {
    fun onNetworkChanged(isConnected: Boolean)
}