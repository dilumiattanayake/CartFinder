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
            val docRef = certificatesCollection.document(stallId) // Using stallId as docId for 1:1 mapping in simple cases
            val cert = Certificate(
                id = stallId,
                stallId = stallId,
                status = "PENDING_REQUEST"
            )
            docRef.set(cert).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun issueCertificate(certificate: Certificate): Result<Unit> {
        return try {
            val docRef = certificatesCollection.document(certificate.stallId)
            docRef.set(certificate).await()
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
            val docRef = alertsCollection.document()
            docRef.set(alert.copy(id = docRef.id)).await()
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
}
