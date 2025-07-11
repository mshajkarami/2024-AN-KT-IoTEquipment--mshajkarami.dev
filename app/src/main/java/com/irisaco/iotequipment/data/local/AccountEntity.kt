package com.irisaco.iotequipment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val username: String,
    val displayName: String,
    val profileImageUrl: String? = null,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val isActive: Boolean = false
)
