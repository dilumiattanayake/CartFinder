package com.sjay.cartfinder.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sjay.cartfinder.data.model.Cart
import com.sjay.cartfinder.data.model.CartItem
import kotlinx.coroutines.tasks.await

class CartRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAllCarts(userId: String): Result<List<Cart>> {
        return try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("carts")
                .get().await()
            val carts = snapshot.documents.mapNotNull { it.toObject(Cart::class.java) }
            Result.success(carts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCart(userId: String, stallId: String): Result<Cart> {
        return try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("carts").document(stallId)
                .get().await()
            val cart = snapshot.toObject(Cart::class.java) ?: Cart(stallId = stallId)
            Result.success(cart)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCart(userId: String, cart: Cart): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .collection("carts").document(cart.stallId)
                .set(cart).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(userId: String, stallId: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .collection("carts").document(stallId)
                .delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
