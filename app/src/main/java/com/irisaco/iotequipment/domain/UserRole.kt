package com.irisaco.iotequipment.domain

enum class UserRole {
    ADMIN,
    ENGINEER,
    USER1,
    USER2,
    UNKNOWN;

    companion object {
        fun fromAccount(roles: List<String>): UserRole {
            return when {
                roles.contains("admin") -> ADMIN
                roles.contains("engineer") -> ENGINEER
                roles.contains("user1") -> USER1
                roles.contains("user2") -> USER2
                else -> UNKNOWN
            }
        }
    }
}
