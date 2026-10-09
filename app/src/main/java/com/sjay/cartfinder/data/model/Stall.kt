package com.sjay.cartfinder.data.model

data class Stall(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val phone: String? = null,
    val imageUrl: String? = null,
    val location: Location = Location(),
    val openingHours: String = "",
    val ratingAverage: Double = 0.0,
    val ratingCount: Int = 0,
    val status: String = "ACTIVE",
    val isOpen: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String? = null,
    val city: String? = null,
    val district: String? = null,
    val mapProvider: String = "OPENSTREETMAP"
)
