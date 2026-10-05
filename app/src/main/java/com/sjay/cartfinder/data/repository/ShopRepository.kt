package com.sjay.cartfinder.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sjay.cartfinder.data.model.MenuItem
import com.sjay.cartfinder.data.model.Stall
import kotlinx.coroutines.tasks.await

class ShopRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val stallsCollection = firestore.collection("stalls")

    // --- Stall CRUD ---

    suspend fun createStall(stall: Stall): Result<String> {
        return try {
            val docRef = if (stall.id.isEmpty()) {
                stallsCollection.document()
            } else {
                stallsCollection.document(stall.id)
            }
            
            val stallWithId = stall.copy(id = docRef.id)
            docRef.set(stallWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStall(stall: Stall): Result<Unit> {
        return try {
            stallsCollection.document(stall.id)
                .set(stall.copy(updatedAt = System.currentTimeMillis()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStallByOwner(ownerId: String): Result<Stall?> {
        return try {
            val snapshot = stallsCollection
                .whereEqualTo("ownerId", ownerId)
                .limit(1)
                .get()
                .await()
            
            val stall = snapshot.documents.firstOrNull()?.toObject(Stall::class.java)
            Result.success(stall)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllStalls(): Result<List<Stall>> {
        return try {
            val snapshot = stallsCollection
                .whereEqualTo("status", "ACTIVE")
                .get()
                .await()
            val stalls = snapshot.documents.mapNotNull { it.toObject(Stall::class.java) }
            Result.success(stalls)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- MenuItem CRUD ---

    suspend fun addMenuItem(stallId: String, menuItem: MenuItem): Result<String> {
        return try {
            val menuCollection = stallsCollection.document(stallId).collection("menuItems")
            val docRef = if (menuItem.id.isEmpty()) {
                menuCollection.document()
            } else {
                menuCollection.document(menuItem.id)
            }
            
            val itemWithId = menuItem.copy(id = docRef.id, stallId = stallId)
            docRef.set(itemWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMenuItem(stallId: String, menuItem: MenuItem): Result<Unit> {
        return try {
            stallsCollection.document(stallId).collection("menuItems").document(menuItem.id)
                .set(menuItem.copy(updatedAt = System.currentTimeMillis()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMenuItemsForStall(stallId: String): Result<List<MenuItem>> {
        return try {
            val snapshot = stallsCollection.document(stallId).collection("menuItems")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val items = snapshot.toObjects(MenuItem::class.java)
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun archiveMenuItem(stallId: String, menuItemId: String): Result<Unit> {
        return try {
            stallsCollection.document(stallId).collection("menuItems").document(menuItemId)
                .update(
                    mapOf(
                        "available" to false,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
