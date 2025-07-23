package com.irisaco.iotequipment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.irisaco.iotequipment.domain.UserRole

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val sub: String,
    val username: String,
    val displayName: String?,
    val email: String?,
    val emailVerified: Boolean = false,
    val givenName: String?,
    val familyName: String?,
    val roles: List<String>,
    val groups: List<String>?,
    val role: UserRole = UserRole.UNKNOWN,
    val isActive: Boolean = false
)
