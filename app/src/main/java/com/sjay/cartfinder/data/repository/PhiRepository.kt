package com.sjay.cartfinder.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sjay.cartfinder.data.model.Certificate
import com.sjay.cartfinder.data.model.Inspection
import com.sjay.cartfinder.data.model.PhiAlert
import kotlinx.coroutines.tasks.await

class PhiRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val certificatesCollection = firestore.collection("certificates")
    private val inspectionsCollection = firestore.collection("inspections")
    private val alertsCollection = firestore.collection("phiAlerts")

    // --- Certificates ---
    suspend fun requestCertificate(stallId: String): Result<Unit> {
        return try {
            // Fetch stall info to enrich the certificate record
            var stallName = ""
            var vendorId = ""
            try {
                val stallDoc = firestore.collection("stalls").document(stallId).get().await()
                stallName = stallDoc.getString("name") ?: ""
                vendorId = stallDoc.getString("vendorId") ?: ""
            } catch (e: Exception) {}

            val docRef = certificatesCollection.document(stallId)
            val cert = Certificate(
                id = stallId,
                stallId = stallId,
                stallName = stallName,
                vendorId = vendorId,
                status = "PENDING_REQUEST",
                requestedAt = System.currentTimeMillis()
            )
            docRef.set(cert).await()

            // Notify all PHI officers
            try {
                val phiUsers = firestore.collection("users").whereEqualTo("role", "PHI").get().await()
                for (doc in phiUsers.documents) {
                    NotificationRepository().sendNotification(
                        com.sjay.cartfinder.data.model.Notification(
                            userId = doc.id,
                            title = "New Certificate Request",
                            message = "${if (stallName.isNotEmpty()) stallName else "A vendor"} has requested a new PHI certificate.",
                            type = "CERTIFICATE_REQUEST",
                            referenceId = stallId
                        )
                    )
                }
            } catch (e: Exception) {}

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun issueCertificate(certificate: Certificate): Result<Unit> {
        return try {
            val docRef = certificatesCollection.document(certificate.stallId)
            docRef.set(certificate).await()

            // Notify Vendor
            try {
                val stallDoc = firestore.collection("stalls").document(certificate.stallId).get().await()
                val vendorId = stallDoc.getString("vendorId")
                if (vendorId != null) {
                    NotificationRepository().sendNotification(
                        com.sjay.cartfinder.data.model.Notification(
                            userId = vendorId,
                            title = "Certificate Issued",
                            message = "Your PHI Certificate has been issued and is now active.",
                            type = "CERTIFICATE_ISSUED",
                            referenceId = certificate.stallId
                        )
                    )
                }
            } catch (e: Exception) {}

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCertificate(stallId: String): Result<Certificate?> {
        return try {
            val snapshot = certificatesCollection.document(stallId).get().await()
            val cert = snapshot.toObject(Certificate::class.java)
            Result.success(cert)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCertificate(stallId: String): Result<Unit> {
        return try {
            certificatesCollection.document(stallId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Inspections ---
    suspend fun addInspection(inspection: Inspection): Result<Unit> {
        return try {
            val docRef = inspectionsCollection.document()
            docRef.set(inspection.copy(id = docRef.id)).await()

            // Notify Vendor
            try {
                val stallDoc = firestore.collection("stalls").document(inspection.stallId).get().await()
                val vendorId = stallDoc.getString("vendorId")
                if (vendorId != null) {
                    NotificationRepository().sendNotification(
                        com.sjay.cartfinder.data.model.Notification(
                            userId = vendorId,
                            title = "New PHI Inspection",
                            message = "A PHI Officer has added a new inspection report for your stall.",
                            type = "PHI_INSPECTION",
                            referenceId = docRef.id
                        )
                    )
                }
            } catch (e: Exception) {}

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateInspection(inspection: Inspection): Result<Unit> {
        return try {
            inspectionsCollection.document(inspection.id).set(inspection).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteInspection(inspectionId: String): Result<Unit> {
        return try {
            inspectionsCollection.document(inspectionId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getInspections(stallId: String): Result<List<Inspection>> {
        return try {
            val snapshot = inspectionsCollection
                .whereEqualTo("stallId", stallId)
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { it.toObject(Inspection::class.java) }
                .sortedByDescending { it.inspectionDate }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Alerts ---
    suspend fun addAlert(alert: PhiAlert): Result<Unit> {
        return try {
            // Enrich with stall name for display
            var enrichedAlert = alert
            try {
                val stallDoc = firestore.collection("stalls").document(alert.stallId).get().await()
                val name = stallDoc.getString("name") ?: ""
                enrichedAlert = alert.copy(stallName = name)
            } catch (e: Exception) {}

            val docRef = alertsCollection.document()
            docRef.set(enrichedAlert.copy(id = docRef.id)).await()

            // Notify Vendor
            try {
                val stallDoc = firestore.collection("stalls").document(alert.stallId).get().await()
                val vendorId = stallDoc.getString("vendorId")
                if (vendorId != null) {
                    NotificationRepository().sendNotification(
                        com.sjay.cartfinder.data.model.Notification(
                            userId = vendorId,
                            title = "PHI Alert!",
                            message = "You have received a new alert from a PHI Officer.",
                            type = "PHI_ALERT",
                            referenceId = docRef.id
                        )
                    )
                }
            } catch (e: Exception) {}

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getActiveAlerts(stallId: String): Result<List<PhiAlert>> {
        return try {
            val snapshot = alertsCollection
                .whereEqualTo("stallId", stallId)
                .whereEqualTo("status", "OPEN")
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { it.toObject(PhiAlert::class.java) }
                .sortedByDescending { it.createdAt }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllActiveAlerts(): Result<List<PhiAlert>> {
        return try {
            val snapshot = alertsCollection
                .whereEqualTo("status", "OPEN")
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { it.toObject(PhiAlert::class.java) }
                .sortedByDescending { it.createdAt }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPendingCertificateRequests(): Result<List<Certificate>> {
        return try {
            val snapshot = certificatesCollection
                .whereEqualTo("status", "PENDING_REQUEST")
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { it.toObject(Certificate::class.java) }
                .sortedByDescending { it.requestedAt }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllCertificates(): Result<List<Certificate>> {
        return try {
            val snapshot = certificatesCollection.get().await()
            val list = snapshot.documents.mapNotNull { it.toObject(Certificate::class.java) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

