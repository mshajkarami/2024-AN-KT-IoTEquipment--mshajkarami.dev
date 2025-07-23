package com.irisaco.iotequipment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.irisaco.iotequipment.domain.UserRole

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val username: String,
    val displayName: String?,
    val profileImageUrl: String?,
    val role: UserRole,
    val isActive: Boolean
)