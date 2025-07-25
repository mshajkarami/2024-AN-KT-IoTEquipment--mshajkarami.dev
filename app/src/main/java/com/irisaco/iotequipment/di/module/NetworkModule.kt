package com.irisaco.iotequipment.di.module

import com.irisaco.iotequipment.data.remote.EquipmentApiService
import com.irisaco.iotequipment.data.repository.AuthRepository
import com.irisaco.iotequipment.ui.auth.AuthInterceptor
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
    private const val URL_API = "http://37.32.10.23:32711/"

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
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitApi(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URL_API)
            .client(client)
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
    fun provideFileDownloadApi(retrofit: Retrofit): FileDownloadApi {
        return retrofit.create(FileDownloadApi::class.java)
    }
}


