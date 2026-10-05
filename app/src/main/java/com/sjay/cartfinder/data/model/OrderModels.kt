package com.sjay.cartfinder.data.model

data class CartItem(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val price: Double = 0.0,
    var quantity: Int = 1,
    val stallId: String = "",
    val imageUrl: String? = null
)

data class Order(
    val id: String = "",
    val customerId: String = "",
    val stallId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: String = "PENDING", // PENDING, PREPARING, READY, COMPLETED, CANCELLED
    val createdAt: Long = System.currentTimeMillis()
)

data class Cart(
    val stallId: String = "",
    val items: List<CartItem> = emptyList()
)
