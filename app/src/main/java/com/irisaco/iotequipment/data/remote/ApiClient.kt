package com.irisaco.iotequipment.data.remote

import com.irisaco.iotequipment.MyApp
import com.irisaco.iotequipment.auth.TokenAuthenticator
import com.irisaco.iotequipment.data.repository.AuthRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/" // یا آدرس سرور Keycloak

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authApi: KeycloakAuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KeycloakAuthService::class.java)
    }

    val authRepository by lazy {
        AuthRepository(
            authService = authApi,
            authTokenDao = MyApp.database.authTokenDao()
        )
    }


    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .authenticator(TokenAuthenticator(authRepository))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: EquipmentApiService by lazy {
        retrofit.create(EquipmentApiService::class.java)
    }

    val authService: KeycloakAuthService by lazy {
        authApi
    }
}
