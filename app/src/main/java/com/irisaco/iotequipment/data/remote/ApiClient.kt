package com.irisaco.iotequipment.data.remote

import android.content.Context
import android.util.Log
import com.irisaco.iotequipment.ui.auth.TokenAuthenticator
import com.irisaco.iotequipment.data.repository.AuthRepository
import com.irisaco.iotequipment.ui.auth.AuthInterceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL_LOCAL = "http://192.168.1.100:8081/"
    private const val BASE_URL_LOCAL2 = "http://192.168.1.100:8080/"

    private lateinit var authRepository: AuthRepository
    private lateinit var client: OkHttpClient
    private lateinit var retrofit: Retrofit

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun init(context: Context) {
        authRepository = AuthRepository(context)

        runBlocking {
            authRepository.loadTokenFromDb()
        }

        client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(authRepository))
            .authenticator(TokenAuthenticator(authRepository))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_LOCAL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    val apiService: EquipmentApiService
        get() = retrofit.create(EquipmentApiService::class.java)

    val authService: KeycloakAuthService
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL_LOCAL2)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KeycloakAuthService::class.java)

    fun getAuthRepository(): AuthRepository = authRepository

    internal inline fun <reified T> createService(): T = retrofit.create(T::class.java)
}

