package com.irisaco.iotequipment.data.repository

import android.content.Context
import com.example.example.Equipment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Responsible for fetching the main equipment data.
// Includes general info like title, description, production date, unique ID, and main photo.
// Use this to retrieve and manage the core equipment entity.

class EquipmentRepository (private val context: Context) {


    fun getEquipment(): List<Equipment> {
        return try {
            val json = context.assets.open("devices.json")
                .bufferedReader()
                .use { it.readText() }

            val equipment = Gson().fromJson(json, Equipment::class.java)

            listOf(equipment)

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}