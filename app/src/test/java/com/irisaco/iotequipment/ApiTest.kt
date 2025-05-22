package com.irisaco.iotequipment

import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


data class Equipment(
    val title: String = "null",
    val id: Int = 0
)
interface ApiService {
    @GET("equipment")
    fun getEquipment(): Call<List<Equipment>>
}
class ApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    @Before
    fun setup(){
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }
    @After
    fun tearDown(){
        mockWebServer.shutdown()
    }
    @Test
    fun testGetEquipment() {
        val  json = this::class.java.getResource("/equipment.json")!!.readText()
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(json)

        mockWebServer.enqueue(mockResponse)

        val response = apiService.getEquipment().execute()
        assert(response.isSuccessful)
        val equipment = response.body()

        assertEquals(2, equipment?.size)
        assertEquals(1, equipment?.get(0)?.id)
        assertEquals(2, equipment?.get(1)?.id)
        assertEquals("پی سیم", equipment?.get(0)?.title)
    }
}