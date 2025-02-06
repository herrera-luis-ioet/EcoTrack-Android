package com.example.ecotrack.data.model

/**
 * Data class representing an environmental activity recorded by a user
 */
data class EcoActivity(
    val id: String = "",
    val userId: String = "",
    val type: ActivityType = ActivityType.OTHER,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val ecoPoints: Int = 0,
    val carbonSaved: Double = 0.0,
    val location: String? = null
)

enum class ActivityType {
    RECYCLING,
    ENERGY_SAVING,
    SUSTAINABLE_TRANSPORT,
    WATER_CONSERVATION,
    OTHER
}