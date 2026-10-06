package com.sjay.cartfinder.data.model

data class MenuItem(
    val id: String = "",
    val stallId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val categoryId: String = "",
    val available: Boolean = true,
    val stockQuantity: Int = 0,
    val preparationTime: Int = 8, // in minutes
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
