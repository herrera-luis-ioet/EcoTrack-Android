package com.example.ecotrack.data.model

/**
 * Data class representing a user profile in the EcoTrack application
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val totalEcoPoints: Int = 0
)