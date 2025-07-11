package com.irisaco.iotequipment.utils

import android.net.Uri
import net.openid.appauth.connectivity.ConnectionBuilder
import java.net.HttpURLConnection
import java.net.URL

object UnsafeConnectionBuilder : ConnectionBuilder {
    override fun openConnection(uri: Uri): HttpURLConnection {
        val url = URL(uri.toString())
        return url.openConnection() as HttpURLConnection
    }
}

