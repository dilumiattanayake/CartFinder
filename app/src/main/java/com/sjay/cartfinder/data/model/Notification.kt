package com.sjay.cartfinder.data.model

data class Notification(
    val id: String = "",
    val userId: String = "", // The recipient of the notification (Customer or Vendor ID)
    val title: String = "",
    val message: String = "",
    val type: String = "INFO", // e.g., ORDER_STATUS, NEW_REVIEW, REVIEW_REPLY
    val referenceId: String = "", // e.g., orderId or reviewId
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
