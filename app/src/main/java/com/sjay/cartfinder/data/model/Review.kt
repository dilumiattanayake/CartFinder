package com.sjay.cartfinder.data.model

data class Review(
    val id: String = "",
    val orderId: String = "",
    val customerId: String = "",
    val stallId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val vendorReply: String? = null,
    val status: String = "ACTIVE", // ACTIVE, REPORTED, ARCHIVED
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
