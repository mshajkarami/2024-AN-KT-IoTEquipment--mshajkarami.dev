package com.irisaco.iotequipment.di.module

import com.irisaco.iotequipment.data.remote.EquipmentApiService
import com.irisaco.iotequipment.data.remote.KeycloakAuthService
import com.irisaco.iotequipment.data.repository.AuthRepository
import com.irisaco.iotequipment.ui.auth.AuthInterceptor
import com.irisaco.iotequipment.ui.auth.TokenAuthenticator
import com.irisaco.iotequipment.utils.FileDownloadApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL_API = "http://192.168.1.100:8081/"
    private const val BASE_URL_AUTH = "http://192.168.1.100:8080/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authRepository: AuthRepository
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(authRepository))
            .authenticator(TokenAuthenticator(authRepository))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitApi(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideRetrofitAuth(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_AUTH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideEquipmentApiService(retrofit: Retrofit): EquipmentApiService {
        return retrofit.create(EquipmentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideKeycloakAuthService(@Named("auth") retrofit: Retrofit): KeycloakAuthService {
        return retrofit.create(KeycloakAuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideFileDownloadApi(retrofit: Retrofit): FileDownloadApi {
        return retrofit.create(FileDownloadApi::class.java)
    }
}


