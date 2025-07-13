package com.irisaco.iotequipment.domain

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(value: String): UserRole {
        return try {
            UserRole.valueOf(value)
        } catch (e: Exception) {
            UserRole.UNKNOWN
        }
    }
}