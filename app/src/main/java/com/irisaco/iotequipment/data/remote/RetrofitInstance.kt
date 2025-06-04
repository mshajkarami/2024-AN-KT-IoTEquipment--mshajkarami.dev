//package com.irisaco.iotequipment.data.remote
//
//import com.google.gson.GsonBuilder
//import com.irisaco.iotequipment.data.remote.ApiClient.retrofit
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import okhttp3.mockwebserver.MockWebServer
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class RetrofitInstance() {
//
//    companion object {
//        val BASE_URL = ("http://10.0.2.2:8080/")
//
//        private val logging = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//
//        private val httpClient = OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .build()
//
//        fun getRetrofitInstance() : Retrofit {
//            return Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .client(httpClient)
//                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
//                .build()
//        }
//    }
//}