package com.sjay.cartfinder.data.model

data class Inspection(
    val id: String = "",
    val stallId: String = "",
    val inspectorId: String = "",
    val inspectionDate: Long = System.currentTimeMillis(),
    val score: Int = 0, // e.g., 0-100
    val result: String = "PENDING", // PASS, FAIL, PENDING
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Certificate(
    val id: String = "",
    val stallId: String = "",
    val vendorId: String = "",
    val stallName: String = "",
    val registrationNumber: String = "",
    val issueDate: Long = System.currentTimeMillis(),
    val expiryDate: Long = System.currentTimeMillis() + 31536000000, // +1 year
    val grade: String = "A", // A, B, C, D
    val score: Int = 100,
    val status: String = "ACTIVE", // ACTIVE, REVOKED, PENDING_REQUEST
    val requestedAt: Long = System.currentTimeMillis()
)

data class PhiAlert(
    val id: String = "",
    val stallId: String = "",
    val stallName: String = "",
    val inspectorId: String = "",
    val title: String = "",
    val description: String = "",
    val severity: String = "LOW", // LOW, MEDIUM, HIGH, CRITICAL
    val status: String = "OPEN", // OPEN, RESOLVED
    val createdAt: Long = System.currentTimeMillis()
)
