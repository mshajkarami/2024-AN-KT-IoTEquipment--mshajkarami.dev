package com.irisaco.iotequipment.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.LiveData


class NetworkConnectionLiveData(context: Context) : LiveData<Boolean>(true) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            postValue(true)
        }

        override fun onLost(network: Network) {
            postValue(true)
        }
    }

    override fun onActive() {
        super.onActive()
        postValue(true)
        try {
            val networkRequest  = NetworkRequest.Builder().build()
            connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
        } catch (_: Exception) {}
    }

    override fun onInactive() {
        super.onInactive()
        try {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } catch (_: Exception) {}
    }
}