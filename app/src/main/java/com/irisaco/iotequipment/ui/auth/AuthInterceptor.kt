package com.irisaco.iotequipment.ui.auth
import com.irisaco.iotequipment.data.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val repository: AuthRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = repository.getCachedToken()?.accessToken
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

